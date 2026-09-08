package ch.minenox.firsttorch.guide.server;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

final class BulkRewardClaimsTest {
    @Test void fullInventoryDoesNotBlockLaterRewards() {
        List<String> attempted = new ArrayList<>();
        var result = BulkRewardClaims.claimIds(List.of("items", "xp"), id -> {
            attempted.add(id);
            return id.equals("items") ? RewardClaims.Result.INVENTORY_FULL : RewardClaims.Result.CLAIMED;
        });
        assertEquals(List.of("items", "xp"), attempted);
        assertEquals(new BulkRewardClaims.Result(1, 1, false), result);
    }

    @Test void unavailableStopsImmediatelyAndReportsAllUnclaimedCandidates() {
        List<String> attempted = new ArrayList<>();
        var result = BulkRewardClaims.claimIds(List.of("first", "broken", "later"), id -> {
            attempted.add(id);
            return id.equals("first") ? RewardClaims.Result.CLAIMED : RewardClaims.Result.UNAVAILABLE;
        });
        assertEquals(List.of("first", "broken"), attempted);
        assertEquals(new BulkRewardClaims.Result(1, 2, true), result);
    }

    @Test void freezesCandidatesBeforeRewardsCanUnlockMoreQuests() {
        List<String> candidates = new ArrayList<>(List.of("first"));
        List<String> attempted = new ArrayList<>();
        var result = BulkRewardClaims.claimIds(candidates, id -> {
            attempted.add(id);
            candidates.add("newly-unlocked");
            return RewardClaims.Result.CLAIMED;
        });
        assertEquals(List.of("first"), attempted);
        assertEquals(new BulkRewardClaims.Result(1, 0, false), result);
    }

    @Test void emptyBatchDoesNotCallClaim() {
        assertEquals(new BulkRewardClaims.Result(0, 0, false), BulkRewardClaims.claimIds(List.of(), id -> {
            fail("No quest should be attempted");
            return RewardClaims.Result.CLAIMED;
        }));
    }
}
