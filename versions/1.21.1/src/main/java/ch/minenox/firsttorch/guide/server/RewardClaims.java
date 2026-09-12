package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.guide.model.RewardDefinition;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;

/** Server-thread-only, whole-quest payouts with durable duplicate protection. */
public final class RewardClaims {
    public enum Result { CLAIMED, ALREADY_CLAIMED, LOCKED, NO_REWARDS, INVENTORY_FULL, UNAVAILABLE }
    public record ClaimView(Set<String> claimedQuestIds, Set<String> pendingQuestIds, boolean available) {}
    private static final Map<MinecraftServer, Map<UUID, RewardJournal>> JOURNALS = new WeakHashMap<>();
    private RewardClaims() {}

    private static RewardJournal journal(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (!server.isSameThread()) throw new IllegalStateException("Rewards require the server thread");
        return JOURNALS.computeIfAbsent(server, ignored -> new HashMap<>()).computeIfAbsent(player.getUUID(),
                id -> new RewardJournal(server.getWorldPath(LevelResource.ROOT).resolve("data")
                        .resolve("firsttorch/reward_claims").resolve(id.toString())));
    }

    public static ClaimView view(ServerPlayer player) {
        RewardJournal journal = journal(player);
        return new ClaimView(journal.ids(RewardJournal.Phase.COMPLETE),
                journal.ids(RewardJournal.Phase.PENDING), journal.available());
    }

    public static Result claim(ServerPlayer player, String questId) {
        if (questId == null || !questId.matches("[0-7][0-9A-F]{15}")) return Result.LOCKED;
        RewardJournal journal = journal(player);
        if (!journal.available()) return Result.UNAVAILABLE;
        if (journal.ids(RewardJournal.Phase.COMPLETE).contains(questId)) return Result.ALREADY_CLAIMED;
        if (journal.ids(RewardJournal.Phase.PENDING).contains(questId)) return Result.UNAVAILABLE;
        try {
            var snapshot = ServerGuideRepository.INSTANCE.snapshot();
            var quest = snapshot.guides().stream().flatMap(guide -> guide.chapters().stream())
                    .flatMap(chapter -> chapter.quests().stream()).filter(value -> value.id().equals(questId))
                    .findFirst().orElse(null);
            if (quest == null) return Result.LOCKED;
            var inventory = player.getInventory();
            Map<String, Integer> counts = ServerTaskEvents.inventory(player);
            var saved = ServerProgressRepository.get(player.level().getServer());
            var progress = TaskEvaluator.evaluate(snapshot, saved.get(player.getUUID()), id -> counts.getOrDefault(id, 0));
            if (!progress.completedQuestIds().contains(questId)
                    || !quest.prerequisitesMet(progress.completedQuestIds())) return Result.LOCKED;
            if (quest.rewards().isEmpty()) return Result.NO_REWARDS;

            List<ItemStack> planned = new ArrayList<>();
            for (int slot = 0; slot < 36; slot++) planned.add(inventory.getItem(slot).copy());
            long experience = 0;
            for (RewardDefinition reward : quest.rewards()) {
                if (reward.amount() <= 0) return Result.UNAVAILABLE;
                if (reward.type() == RewardDefinition.Type.EXPERIENCE) {
                    experience += reward.amount();
                    if (experience > Integer.MAX_VALUE - (long) player.totalExperience) return Result.UNAVAILABLE;
                } else {
                    ResourceLocation itemId = ResourceLocation.parse(reward.itemId());
                    if (!BuiltInRegistries.ITEM.containsKey(itemId)) return Result.UNAVAILABLE;
                    ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(itemId));
                    if (stack.isEmpty()) return Result.UNAVAILABLE;
                    if (!insert(planned, stack, reward.amount())) return Result.INVENTORY_FULL;
                }
            }
            // No item or XP mutation can occur until CREATE_NEW and force(true) have succeeded.
            if (!journal.reserve(questId, quest.rewards())) return Result.UNAVAILABLE;
            for (int slot = 0; slot < 36; slot++) inventory.setItem(slot, planned.get(slot));
            if (experience > 0) player.giveExperiencePoints((int) experience);
            inventory.setChanged();
            player.containerMenu.broadcastChanges();
            ServerProgressRepository.remember(saved, player.getUUID(), progress);
            journal.complete(questId);
            return Result.CLAIMED;
        } catch (IOException | RuntimeException exception) {
            LogUtils.getLogger().error("First Torch reward claim failed for player {} quest {}; preserve the claim journal for manual recovery.",
                    player.getUUID(), questId, exception);
            return Result.UNAVAILABLE;
        }
    }

    static boolean insert(List<ItemStack> slots, ItemStack template, int amount) {
        int remaining = amount;
        for (ItemStack slot : slots) {
            if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, template)) {
                int moved = Math.min(remaining, Math.max(0, slot.getMaxStackSize() - slot.getCount()));
                slot.grow(moved);
                remaining -= moved;
            }
        }
        for (int slot = 0; slot < slots.size() && remaining > 0; slot++) {
            if (slots.get(slot).isEmpty()) {
                int moved = Math.min(remaining, template.getMaxStackSize());
                slots.set(slot, template.copyWithCount(moved));
                remaining -= moved;
            }
        }
        return remaining == 0;
    }
}
