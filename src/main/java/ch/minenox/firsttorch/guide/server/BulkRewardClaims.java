package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.guide.progress.ClaimableRewards;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import net.minecraft.server.level.ServerPlayer;

/** Freezes eligible quests before any payout; each quest keeps its own durable reservation. */
public final class BulkRewardClaims {
    public record Result(int claimed, int skipped, boolean unavailable) {}

    private BulkRewardClaims() {}

    public static Result claim(ServerPlayer player) {
        List<String> candidates;
        try {
            var snapshot = ServerGuideRepository.INSTANCE.snapshot();
            var counts = ServerTaskEvents.inventory(player);
            var saved = ServerProgressRepository.get(player.level().getServer());
            var progress = TaskEvaluator.evaluate(snapshot, saved.get(player.getUUID()),
                    id -> counts.getOrDefault(id, 0));
            var claims = RewardClaims.view(player);
            if (!claims.available()) return new Result(0, 0, true);
            candidates = ClaimableRewards.ids(snapshot, progress, claims.claimedQuestIds(), claims.pendingQuestIds());
        } catch (RuntimeException exception) {
            LogUtils.getLogger().error("First Torch bulk reward selection failed for player {}.", player.getUUID(), exception);
            return new Result(0, 0, true);
        }
        return claimIds(candidates, id -> RewardClaims.claim(player, id));
    }

    static Result claimIds(List<String> candidates, Function<String, RewardClaims.Result> claim) {
        List<String> frozen = List.copyOf(candidates);
        int claimed = 0;
        for (String id : frozen) {
            var result = claim.apply(id);
            if (result == RewardClaims.Result.CLAIMED) claimed++;
            // Full item rewards do not prevent later XP-only rewards from being collected.
            if (result == RewardClaims.Result.UNAVAILABLE) return new Result(claimed, frozen.size() - claimed, true);
        }
        return new Result(claimed, frozen.size() - claimed, false);
    }
}
