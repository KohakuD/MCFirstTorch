package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.network.ProgressPayload;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Server-thread-only evaluation backed by the world's persistent player progress. */
@EventBusSubscriber(modid = "firsttorch")
public final class ServerTaskEvents {
    private static final Map<ServerPlayer, ProgressPayload> LAST_SENT = new WeakHashMap<>();
    private ServerTaskEvents() {}

    static Map<String, Integer> inventory(ServerPlayer player) {
        var inventory = player.getInventory();
        var stacks = new ArrayList<ItemStack>(inventory.getContainerSize());
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            var stack = inventory.getItem(slot);
            if (!stack.isEmpty()) stacks.add(stack);
        }
        Set<String> tagIds = ServerGuideRepository.INSTANCE.snapshot().guides().stream()
                .flatMap(guide -> guide.chapters().stream())
                .flatMap(chapter -> chapter.quests().stream())
                .flatMap(quest -> quest.tasks().stream())
                .filter(task -> task.type() == TaskDefinition.Type.INVENTORY_TAG)
                .map(TaskDefinition::itemId).collect(java.util.stream.Collectors.toUnmodifiableSet());
        var counts = new java.util.HashMap<>(InventoryCounts.collect(stacks,
                stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), ItemStack::getCount, tagIds,
                (stack, tagId) -> stack.is(TagKey.create(Registries.ITEM, ResourceLocation.parse(tagId)))));
        ServerGuideRepository.INSTANCE.snapshot().guides().stream()
                .flatMap(guide -> guide.chapters().stream()).flatMap(chapter -> chapter.quests().stream())
                .flatMap(quest -> quest.tasks().stream()).filter(task -> task.type() == TaskDefinition.Type.ADVANCEMENT)
                .forEach(task -> counts.computeIfAbsent(task.inventoryKey(), ignored -> {
                    var holder = player.level().getServer().getAdvancements().get(ResourceLocation.parse(task.advancementId()));
                    if (holder == null) return 0;
                    var advancement = player.getAdvancements().getOrStartProgress(holder);
                    return AdvancementObservation.count(advancement, task.criterion());
                }));
        return Map.copyOf(counts);
    }

    private static ProgressState evaluate(ServerPlayer player, boolean force) {
        var states = ServerProgressRepository.get(player.level().getServer());
        var counts = inventory(player);
        ProgressState next = TaskEvaluator.evaluate(ServerGuideRepository.INSTANCE.snapshot(),
                states.get(player.getUUID()), id -> counts.getOrDefault(id, 0));
        ServerProgressRepository.remember(states, player.getUUID(), next);
        send(player, ProgressObservation.create(ServerGuideRepository.INSTANCE.snapshot(), next, counts), force);
        return next;
    }

    private static void send(ServerPlayer player, ProgressPayload payload, boolean force) {
        if (payload.available()) {
            var claims = RewardClaims.view(player);
            try {
                var rewardIds = ServerGuideRepository.INSTANCE.snapshot().guides().stream()
                        .flatMap(guide -> guide.chapters().stream()).flatMap(chapter -> chapter.quests().stream())
                        .filter(quest -> !quest.rewards().isEmpty()).map(quest -> quest.id())
                        .collect(java.util.stream.Collectors.toSet());
                payload = ProgressObservation.withClaims(payload, claims.claimedQuestIds(),
                        claims.pendingQuestIds(), claims.available(), rewardIds);
            } catch (IllegalArgumentException exception) {
                payload = ProgressPayload.UNAVAILABLE;
            }
        }
        if (force || !payload.equals(LAST_SENT.get(player))) {
            PacketDistributor.sendToPlayer(player, payload);
            LAST_SENT.put(player, payload);
        }
    }

    public static void sync(ServerPlayer player, boolean force) {
        try {
            evaluate(player, force);
        } catch (IllegalStateException | IllegalArgumentException exception) {
            // Do not retain a stale client display if storage or transport limits prevent an observation.
            send(player, ProgressPayload.UNAVAILABLE, force);
        }
    }

    @SubscribeEvent
    public static void tick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player && player.tickCount % 20 == 0) {
            sync(player, false);
        }
    }

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("firsttorch")
                .then(Commands.literal("status").executes(context -> {
                    ProgressState state;
                    try {
                        state = evaluate(context.getSource().getPlayerOrException(), false);
                    } catch (IllegalStateException exception) {
                        send(context.getSource().getPlayerOrException(), ProgressPayload.UNAVAILABLE, false);
                        context.getSource().sendFailure(Component.translatable("command.firsttorch.storage.unavailable"));
                        return 0;
                    }
                    context.getSource().sendSuccess(() -> Component.translatable("command.firsttorch.status",
                            state.completedQuestIds().size(), state.completedTaskIds().size()), false);
                    return 1;
                }))
                .then(Commands.literal("confirm")
                        .then(Commands.argument("quest", StringArgumentType.word())
                                .then(Commands.argument("task", StringArgumentType.word()).executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    String quest = StringArgumentType.getString(context, "quest");
                                    String task = StringArgumentType.getString(context, "task");
                                    if (quest.length() != 16 || task.length() != 16) {
                                        context.getSource().sendFailure(Component.translatable("command.firsttorch.confirm.rejected"));
                                        return 0;
                                    }
                                    var counts = inventory(player);
                                    try {
                                        var data = ServerProgressRepository.get(player.level().getServer());
                                        ProgressState next = TaskEvaluator.confirm(ServerGuideRepository.INSTANCE.snapshot(),
                                                data.get(player.getUUID()),
                                                quest, task, id -> counts.getOrDefault(id, 0));
                                        ServerProgressRepository.remember(data, player.getUUID(), next);
                                        send(player, ProgressObservation.create(ServerGuideRepository.INSTANCE.snapshot(),
                                                next, counts), false);
                                        context.getSource().sendSuccess(
                                                () -> Component.translatable("command.firsttorch.confirm.accepted"), false);
                                        return 1;
                                    } catch (IllegalStateException exception) {
                                        send(player, ProgressPayload.UNAVAILABLE, false);
                                        context.getSource().sendFailure(Component.translatable("command.firsttorch.storage.unavailable"));
                                        return 0;
                                    } catch (IllegalArgumentException exception) {
                                        context.getSource().sendFailure(Component.translatable("command.firsttorch.confirm.rejected"));
                                        return 0;
                                    }
                                })))));
    }
}
