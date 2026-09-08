package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.FirstTorch;
import ch.minenox.firsttorch.guide.progress.DevelopmentQuestAccess;
import ch.minenox.firsttorch.guide.progress.DevelopmentQuestCompletion;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** No multiplayer/admin bypass: this utility is exclusively for the opted-in local world owner. */
@EventBusSubscriber(modid = FirstTorch.MOD_ID)
public final class DevelopmentQuestCommands {
    private DevelopmentQuestCommands() {}

    private static boolean allowed(ServerPlayer player) {
        var server = player.level().getServer();
        var owner = server.getSingleplayerProfile();
        return DevelopmentQuestAccess.allowed(DevelopmentQuestAccess.enabled(), server.isSingleplayer(),
                owner == null ? null : owner.id(), player.getUUID());
    }

    @SubscribeEvent public static void commands(RegisterCommandsEvent event) {
        if (!DevelopmentQuestAccess.enabled()) return;
        event.getDispatcher().register(Commands.literal("firsttorch")
                .then(Commands.literal("test_complete")
                        .requires(source -> source.getEntity() instanceof ServerPlayer player && allowed(player))
                        .then(Commands.argument("quest", StringArgumentType.word()).executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            if (!allowed(player)) return 0;
                            try {
                                var data = ServerProgressRepository.get(player.level().getServer());
                                var state = DevelopmentQuestCompletion.complete(ServerGuideRepository.INSTANCE.snapshot(),
                                        data.get(player.getUUID()), StringArgumentType.getString(context, "quest"));
                                ServerProgressRepository.remember(data, player.getUUID(), state);
                                ServerTaskEvents.sync(player, true);
                                context.getSource().sendSuccess(() -> Component.translatable("command.firsttorch.test.completed"), false);
                                return 1;
                            } catch (IllegalArgumentException | IllegalStateException exception) {
                                context.getSource().sendFailure(Component.translatable("command.firsttorch.test.failed"));
                                return 0;
                            }
                        }))));
    }
}
