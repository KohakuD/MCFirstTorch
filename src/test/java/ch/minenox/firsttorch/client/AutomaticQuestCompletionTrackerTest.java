package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.model.RewardDefinition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class AutomaticQuestCompletionTrackerTest {
    private static final String AUTO = "1000000000000001";
    private static final String MANUAL = "1000000000000002";

    private GuideSnapshot guides() {
        var auto = new QuestDefinition(AUTO, 0, "quest.auto", "quest.description", new QuestPosition(0, 0), List.of(),
                List.of(new TaskDefinition("3000000000000001", TaskDefinition.Type.INVENTORY, "minecraft:stone_axe", 1)),
                List.of(new RewardDefinition("4000000000000001", RewardDefinition.Type.EXPERIENCE, null, 3)));
        var manual = new QuestDefinition(MANUAL, 1, "quest.manual", "quest.description", new QuestPosition(1, 0), List.of(),
                List.of(new TaskDefinition("3000000000000002", TaskDefinition.Type.MANUAL, null, 1)), List.of());
        return new GuideSnapshot(List.of(new GuideDefinition(1, "0000000000000001", "guide.title", "guide.description", List.of(
                new ChapterDefinition("2000000000000001", 0, "chapter.title", "chapter.description", List.of(auto, manual))))));
    }

    private ProgressPayload progress(Set<String> completed) {
        return new ProgressPayload(new ProgressState(Set.of(), completed), Map.of(), true);
    }

    @Test void initialSnapshotAndUnavailableStateAreSilent() {
        var tracker = new AutomaticQuestCompletionTracker();
        assertTrue(tracker.observe(guides(), ProgressPayload.UNAVAILABLE).isEmpty());
        assertTrue(tracker.observe(guides(), progress(Set.of(AUTO))).isEmpty());
        tracker.observe(guides(), progress(Set.of()));
        assertTrue(tracker.observe(guides(), ProgressPayload.UNAVAILABLE).isEmpty());
        assertTrue(tracker.observe(guides(), progress(Set.of(AUTO))).isEmpty());
    }

    @Test void reportsOnlyNewAutomaticQuestAndOffersUnclaimedReward() {
        var tracker = new AutomaticQuestCompletionTracker();
        tracker.observe(guides(), progress(Set.of()));
        var notices = tracker.observe(guides(), progress(Set.of(AUTO, MANUAL)));
        assertEquals(1, notices.size());
        assertEquals(AUTO, notices.getFirst().questId());
        assertTrue(notices.getFirst().rewardAvailable());
        assertTrue(tracker.observe(guides(), progress(Set.of(AUTO, MANUAL))).isEmpty());
    }

    @Test void claimedOrInterruptedRewardsDoNotPromiseAvailability() {
        var tracker = new AutomaticQuestCompletionTracker();
        tracker.observe(guides(), progress(Set.of()));
        var claimed = new ProgressPayload(new ProgressState(Set.of(), Set.of(AUTO)), Map.of(), true, Set.of(AUTO), Set.of());
        assertFalse(tracker.observe(guides(), claimed).getFirst().rewardAvailable());
        tracker.clear();
        tracker.observe(guides(), progress(Set.of()));
        var pending = new ProgressPayload(new ProgressState(Set.of(), Set.of(AUTO)), Map.of(), true, Set.of(), Set.of(AUTO));
        assertFalse(tracker.observe(guides(), pending).getFirst().rewardAvailable());
    }

    @Test void taskCompletionAloneDoesNotAnnounceAQuest() {
        var tracker = new AutomaticQuestCompletionTracker();
        tracker.observe(guides(), progress(Set.of()));
        var tasksOnly = new ProgressPayload(new ProgressState(Set.of("3000000000000001"), Set.of()), Map.of(), true);
        assertTrue(tracker.observe(guides(), tasksOnly).isEmpty());
        assertEquals(1, tracker.observe(guides(), progress(Set.of(AUTO))).size());
    }
}
