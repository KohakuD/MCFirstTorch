package ch.minenox.firsttorch.guide.progress;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import java.util.List;
import java.util.Set;

/** Shared eligibility only; actual payouts always require server validation. */
public final class ClaimableRewards {
    private ClaimableRewards() {}

    public static List<String> ids(GuideSnapshot snapshot, ProgressState progress,
            Set<String> claimed, Set<String> pending) {
        return snapshot.guides().stream().flatMap(g -> g.chapters().stream())
                .flatMap(c -> c.quests().stream())
                .filter(q -> !q.rewards().isEmpty()
                        && progress.completedQuestIds().contains(q.id())
                        && q.prerequisitesMet(progress.completedQuestIds())
                        && !claimed.contains(q.id()) && !pending.contains(q.id()))
                .map(q -> q.id()).distinct().sorted().toList();
    }
}
