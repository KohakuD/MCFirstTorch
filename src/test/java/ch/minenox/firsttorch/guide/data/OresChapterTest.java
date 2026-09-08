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

final class OresChapterTest {
    private static final List<String> QUEST_IDS = List.of("0A73D9E14C628BF5", "19C5E2A70B864DF3",
            "2B84F1C60D735AE9", "3C16B9E50A724DF8", "4A28C6E10D735BF9", "6C4AE8F31D957B20");
    private static final List<String> TASK_IDS = List.of("4E16B8C30D795AF2", "57A0D4C91E326BF8",
            "0D39A7E25C614BF8", "61E4A8C20D935BF7", "5B39D7F20E846AC1", "7D5BF9042EA68C31");

    @Test void preservesTheSixSourceOresLessonsAndTheirExactObjectives() throws Exception {
        var guide = course();
        var chapter = guide.chapters().get(5);
        assertEquals("3B75D9F21C8E406A", chapter.id());
        assertEquals(5, chapter.order());
        assertEquals("minecraft:iron_ore", chapter.iconItemId());
        assertEquals(QUEST_IDS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(TASK_IDS, chapter.quests().stream().map(q -> q.tasks().getFirst().id()).toList());
        assertEquals(TaskDefinition.Type.MANUAL, chapter.quests().getFirst().tasks().getFirst().type());
        assertEquals(List.of("minecraft:stone_pickaxe", "minecraft:raw_iron", "minecraft:iron_ingot",
                "minecraft:raw_copper", "minecraft:copper_ingot"), chapter.quests().stream().skip(1)
                .map(q -> q.tasks().getFirst().itemId()).toList());
        assertTrue(chapter.quests().stream().flatMap(q -> q.rewards().stream()).toList().isEmpty());
        assertEquals(List.of("6C03B5E98A417DF2"), chapter.quests().getFirst().prerequisiteQuestIds());
        assertEquals(List.of("0A73D9E14C628BF5"), chapter.quests().get(1).prerequisiteQuestIds());
        assertEquals(List.of("19C5E2A70B864DF3"), chapter.quests().get(2).prerequisiteQuestIds());
        assertEquals(List.of("2B84F1C60D735AE9"), chapter.quests().get(3).prerequisiteQuestIds());
        assertEquals(List.of("19C5E2A70B864DF3"), chapter.quests().get(4).prerequisiteQuestIds());
        assertEquals(List.of("4A28C6E10D735BF9"), chapter.quests().get(5).prerequisiteQuestIds());
    }

    @Test void inventoryConditionsAreAutomaticWhileRetreatCannotBeConfirmedBeforeFoodReserve() throws Exception {
        var guide = course();
        var snapshot = new GuideSnapshot(List.of(guide));
        var items = Set.of("minecraft:stone_pickaxe", "minecraft:raw_iron", "minecraft:iron_ingot",
                "minecraft:raw_copper", "minecraft:copper_ingot");
        var observed = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> items.contains(key) ? 1 : 0);
        assertTrue(observed.completedTaskIds().containsAll(TASK_IDS.subList(1, 6)));
        assertTrue(observed.completedQuestIds().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, observed,
                "0A73D9E14C628BF5", "4E16B8C30D795AF2", key -> 0));

        var earlier = guide.chapters().stream().limit(5).flatMap(chapter -> chapter.quests().stream()).toList();
        var tasks = earlier.stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).collect(Collectors.toSet());
        tasks.addAll(observed.completedTaskIds());
        var completed = new ProgressState(tasks,
                earlier.stream().map(q -> q.id()).collect(Collectors.toSet()));
        var available = TaskEvaluator.confirm(snapshot, completed, "0A73D9E14C628BF5", "4E16B8C30D795AF2", key -> 0);
        assertTrue(available.completedQuestIds().containsAll(QUEST_IDS));
    }

    @Test void ironBranchDoesNotRequireCopper() throws Exception {
        var guide = course();
        var snapshot = new GuideSnapshot(List.of(guide));
        var earlier = guide.chapters().stream().limit(5).flatMap(chapter -> chapter.quests().stream()).toList();
        var done = earlier.stream().map(q -> q.id()).collect(Collectors.toSet());
        var tasks = earlier.stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).collect(Collectors.toSet());
        var ironItems = Set.of("minecraft:stone_pickaxe", "minecraft:raw_iron", "minecraft:iron_ingot");
        var result = TaskEvaluator.confirm(snapshot, new ProgressState(tasks, done), QUEST_IDS.getFirst(), TASK_IDS.getFirst(),
                key -> ironItems.contains(key) ? 1 : 0);
        assertTrue(result.completedQuestIds().containsAll(QUEST_IDS.subList(0, 4)));
        assertFalse(result.completedQuestIds().contains(QUEST_IDS.get(4)));
        assertFalse(result.completedQuestIds().contains(QUEST_IDS.get(5)));
    }

    private static ch.minenox.firsttorch.guide.model.GuideDefinition course() throws Exception {
        try (var input = OresChapterTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            assertNotNull(input);
            return GuideJson.read(input);
        }
    }
}
