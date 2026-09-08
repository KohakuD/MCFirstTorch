package ch.minenox.firsttorch.guide.progress;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.data.GuideJson;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

final class ClaimableRewardsTest {
    @Test void emptyProgressAndClaimedOrPendingRewardsAreHidden() throws Exception {
        var snapshot = course();
        assertTrue(ClaimableRewards.ids(snapshot, ProgressState.EMPTY, Set.of(), Set.of()).isEmpty());
        var all = snapshot.guides().stream().flatMap(g -> g.chapters().stream())
                .flatMap(c -> c.quests().stream()).toList();
        var state = new ProgressState(Set.of(), all.stream().map(q -> q.id()).collect(Collectors.toSet()));
        var eligible = ClaimableRewards.ids(snapshot, state, Set.of(), Set.of());
        assertEquals(all.stream().filter(q -> !q.rewards().isEmpty()).count(), eligible.size());
        assertTrue(ClaimableRewards.ids(snapshot, state, Set.copyOf(eligible), Set.of()).isEmpty());
        assertTrue(ClaimableRewards.ids(snapshot, state, Set.of(), Set.copyOf(eligible)).isEmpty());
    }

    @Test void completedQuestStillNeedsItsPrerequisites() throws Exception {
        var snapshot = course();
        var quest = snapshot.guides().getFirst().chapters().stream().flatMap(c -> c.quests().stream())
                .filter(q -> !q.rewards().isEmpty() && !q.prerequisiteQuestIds().isEmpty()).findFirst().orElseThrow();
        assertTrue(ClaimableRewards.ids(snapshot, new ProgressState(Set.of(), Set.of(quest.id())),
                Set.of(), Set.of()).isEmpty());
        var completed = new java.util.HashSet<>(quest.prerequisiteQuestIds());
        completed.add(quest.id());
        assertTrue(ClaimableRewards.ids(snapshot, new ProgressState(Set.of(), completed),
                Set.of(), Set.of()).contains(quest.id()));
    }

    private GuideSnapshot course() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(java.util.List.of(GuideJson.read(input)));
        }
    }
}
