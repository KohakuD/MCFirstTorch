package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

final class ClosingLessonsTest {
    @Test void preservesSourceClosingGateAndRewards() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var guide = GuideJson.read(input);
            var quests = guide.chapters().stream().flatMap(c -> c.quests().stream())
                    .collect(Collectors.toMap(q -> q.id(), q -> q));
            var closing = List.of("3286E0194CA735DF", "6E9B173C5D802AF4", "54A8023B6EC957F1").stream().map(quests::get).toList();
            assertEquals(List.of("3286E0194CA735DF", "6E9B173C5D802AF4", "54A8023B6EC957F1"), closing.stream().map(q -> q.id()).toList());
            var bed = closing.get(0); var upgrade = closing.get(1); var morning = closing.get(2);
            assertEquals(List.of("1064CEF72A8513BD"), bed.prerequisiteQuestIds());
            assertEquals(Set.of("7E42ACD50863F19B", "1064CEF72A8513BD"), Set.copyOf(upgrade.prerequisiteQuestIds()));
            assertEquals(Set.of(bed.id(), upgrade.id()), Set.copyOf(morning.prerequisiteQuestIds()));
            var sleep = bed.tasks().getFirst();
            assertEquals("4397F12A5DB846E0", sleep.id());
            assertEquals(TaskDefinition.Type.ADVANCEMENT, sleep.type());
            assertEquals("minecraft:adventure/sleep_in_bed", sleep.advancementId());
            assertEquals("slept_in_bed", sleep.criterion());
            assertEquals(List.of("37AF205CE961B4D8", "5E18C7D042AB936F"), morning.rewards().stream().map(r -> r.id()).toList());
            assertEquals(List.of("minecraft:bread", "minecraft:lantern"), morning.rewards().stream().map(r -> r.itemId()).toList());
            assertEquals(List.of(3, 1), morning.rewards().stream().map(r -> r.amount()).toList());
            var snapshot = new GuideSnapshot(List.of(guide));
            var earlier = guide.chapters().stream().limit(4).flatMap(c -> c.quests().stream()).filter(q -> !closing.contains(q)).toList();
            var state = new ProgressState(earlier.stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).collect(Collectors.toSet()),
                    earlier.stream().map(q -> q.id()).collect(Collectors.toSet()));
            var slept = TaskEvaluator.evaluate(snapshot, state, key -> key.equals(sleep.inventoryKey()) ? 1 : 0);
            assertTrue(slept.completedQuestIds().contains(bed.id()));
            assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, slept, morning.id(), morning.tasks().getFirst().id(), key -> 0));
            var upgraded = TaskEvaluator.confirm(snapshot, slept, upgrade.id(), upgrade.tasks().getFirst().id(), key -> 0);
            var finished = TaskEvaluator.confirm(snapshot, upgraded, morning.id(), morning.tasks().getFirst().id(), key -> 0);
            assertTrue(finished.completedQuestIds().containsAll(closing.stream().map(q -> q.id()).toList()));
        }
    }
}
