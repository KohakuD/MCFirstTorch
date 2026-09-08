package ch.minenox.firsttorch.guide.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.model.RewardDefinition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

final class QuestRequirementsTest {
    private static final String TASK_ID = "3A13F17C00000001";
    private static final String REWARD_ID = "4A13F17C00000001";

    @Test
    void preservesOldConstructorAndOmittedJsonFields() {
        QuestDefinition old = new QuestDefinition("2A13F17C00000001", 0,
                "quest.test.title", "quest.test.description", new QuestPosition(0, 0), null);
        QuestDefinition decoded = new Gson().fromJson("""
                {"id":"2A13F17C00000001","order":0,"titleKey":"quest.test.title",
                 "descriptionKey":"quest.test.description","position":{"x":0,"y":0}}
                """, QuestDefinition.class);
        assertEquals(old, decoded);
        assertTrue(old.tasks().isEmpty());
        assertTrue(old.rewards().isEmpty());
        assertDoesNotThrow(() -> GuideValidator.validate(guide(old, 1)));
    }

    @Test
    void acceptsAllSupportedTypesAndMaximumAmountsThroughGson() {
        QuestDefinition quest = quest(List.of(
                task(TaskDefinition.Type.MANUAL, null, 1),
                new TaskDefinition("3A13F17C00000002", TaskDefinition.Type.INVENTORY,
                        "minecraft:torch", 4096)), List.of(
                reward(RewardDefinition.Type.EXPERIENCE, null, 10000),
                new RewardDefinition("4A13F17C00000002", RewardDefinition.Type.ITEM,
                        "example:tools/torch", 4096)));
        Gson gson = new Gson();
        GuideDefinition guide = guide(quest, 1);
        GuideDefinition decoded = gson.fromJson(gson.toJson(guide), GuideDefinition.class);
        assertEquals(guide, decoded);
        assertDoesNotThrow(() -> GuideSetValidator.validate(List.of(decoded)));
    }

    @Test
    void rejectsInvalidTaskSemantics() {
        for (TaskDefinition task : List.of(
                task(null, null, 1),
                task(TaskDefinition.Type.MANUAL, "minecraft:torch", 1),
                task(TaskDefinition.Type.MANUAL, null, 0),
                task(TaskDefinition.Type.MANUAL, null, 2),
                task(TaskDefinition.Type.INVENTORY, null, 1),
                task(TaskDefinition.Type.INVENTORY, "minecraft:torch", 0),
                task(TaskDefinition.Type.INVENTORY, "minecraft:torch", 4097))) {
            assertInvalid(quest(List.of(task), List.of()));
        }
    }

    @Test
    void rejectsInvalidRewardSemantics() {
        for (RewardDefinition reward : List.of(
                reward(null, null, 1),
                reward(RewardDefinition.Type.EXPERIENCE, "minecraft:torch", 1),
                reward(RewardDefinition.Type.EXPERIENCE, null, 0),
                reward(RewardDefinition.Type.EXPERIENCE, null, 10001),
                reward(RewardDefinition.Type.ITEM, null, 1),
                reward(RewardDefinition.Type.ITEM, "minecraft:torch", 0),
                reward(RewardDefinition.Type.ITEM, "minecraft:torch", 4097))) {
            assertInvalid(quest(List.of(), List.of(reward)));
        }
    }

    @Test
    void rejectsInvalidItemIdentifiersForTasksAndRewards() {
        for (String itemId : List.of("torch", "Minecraft:torch", "minecraft:", ":torch",
                "minecraft:torch extra", "minecraft:torch#tag", "minecraft:" + "x".repeat(247))) {
            assertInvalid(quest(List.of(task(TaskDefinition.Type.INVENTORY, itemId, 1)), List.of()));
            assertInvalid(quest(List.of(), List.of(reward(RewardDefinition.Type.ITEM, itemId, 1))));
        }
    }

    @Test
    void rejectsUnknownJsonTypeAndMissingAmount() {
        Gson gson = new Gson();
        for (String json : List.of(
                "{\"id\":\"3A13F17C00000001\",\"type\":\"KILL\",\"count\":1}",
                "{\"id\":\"3A13F17C00000001\",\"type\":\"MANUAL\"}")) {
            assertInvalid(quest(List.of(gson.fromJson(json, TaskDefinition.class)), List.of()));
        }
        RewardDefinition unknown = gson.fromJson(
                "{\"id\":\"4A13F17C00000001\",\"type\":\"RANDOM\",\"amount\":1}", RewardDefinition.class);
        assertInvalid(quest(List.of(), List.of(unknown)));
    }

    @Test
    void enforcesNestedStableIdsAndGlobalUniqueness() {
        assertInvalid(quest(List.of(new TaskDefinition("8A13F17C00000001",
                TaskDefinition.Type.MANUAL, null, 1)), List.of()));
        assertInvalid(quest(List.of(task(TaskDefinition.Type.MANUAL, null, 1)),
                List.of(new RewardDefinition(TASK_ID, RewardDefinition.Type.EXPERIENCE, null, 1))));
        assertInvalid(quest(List.of(), List.of(new RewardDefinition("0A13F17C00000001",
                RewardDefinition.Type.EXPERIENCE, null, 1))));
        QuestDefinition first = quest(List.of(task(TaskDefinition.Type.MANUAL, null, 1)), List.of());
        QuestDefinition second = new QuestDefinition("2A13F17C00000002", 0,
                "quest.test.title", "quest.test.description", new QuestPosition(0, 0), List.of(),
                List.of(), List.of(new RewardDefinition(TASK_ID, RewardDefinition.Type.EXPERIENCE, null, 1)));
        assertThrows(GuideValidationException.class,
                () -> GuideSetValidator.validate(List.of(guide(first, 1), guide(second, 2))));
    }

    @Test
    void capsCollectionsAndCopiesTheirContents() {
        List<TaskDefinition> tasks = IntStream.range(0, 33).mapToObj(index ->
                new TaskDefinition(String.format("3A13F17C%08X", index), TaskDefinition.Type.MANUAL, null, 1)).toList();
        List<RewardDefinition> rewards = IntStream.range(0, 33).mapToObj(index ->
                new RewardDefinition(String.format("4A13F17C%08X", index), RewardDefinition.Type.EXPERIENCE, null, 1)).toList();
        assertDoesNotThrow(() -> GuideValidator.validate(guide(quest(tasks.subList(0, 32), rewards.subList(0, 32)), 1)));
        assertInvalid(quest(tasks, List.of()));
        assertInvalid(quest(List.of(), rewards));
        ArrayList<TaskDefinition> mutable = new ArrayList<>(tasks.subList(0, 1));
        QuestDefinition snapshot = quest(mutable, null);
        mutable.clear();
        assertEquals(1, snapshot.tasks().size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.tasks().clear());
        assertTrue(snapshot.rewards().isEmpty());
    }

    private static TaskDefinition task(TaskDefinition.Type type, String itemId, int count) {
        return new TaskDefinition(TASK_ID, type, itemId, count);
    }

    private static RewardDefinition reward(RewardDefinition.Type type, String itemId, int amount) {
        return new RewardDefinition(REWARD_ID, type, itemId, amount);
    }

    private static QuestDefinition quest(List<TaskDefinition> tasks, List<RewardDefinition> rewards) {
        return new QuestDefinition("2A13F17C00000001", 0, "quest.test.title", "quest.test.description",
                new QuestPosition(0, 0), List.of(), tasks, rewards);
    }

    private static GuideDefinition guide(QuestDefinition quest, int suffix) {
        return new GuideDefinition(1, String.format("0A13F17C%08X", suffix), "guide.test.title",
                "guide.test.description", List.of(new ChapterDefinition(String.format("1A13F17C%08X", suffix),
                0, "chapter.test.title", "chapter.test.description", List.of(quest))));
    }

    private static void assertInvalid(QuestDefinition quest) {
        assertThrows(GuideValidationException.class, () -> GuideValidator.validate(guide(quest, 1)));
    }
}
