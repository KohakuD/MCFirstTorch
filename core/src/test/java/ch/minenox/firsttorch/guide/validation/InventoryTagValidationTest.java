package ch.minenox.firsttorch.guide.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import com.google.gson.Gson;
import java.util.List;
import org.junit.jupiter.api.Test;

final class InventoryTagValidationTest {
    @Test
    void acceptsNamespacedTagWithoutHash() {
        assertDoesNotThrow(() -> GuideValidator.validate(guide(
                new TaskDefinition("3000000000000001", TaskDefinition.Type.INVENTORY_TAG, "minecraft:planks", 4096))));
    }

    @Test
    void rejectsInvalidTagIdentifiersCountsAndUnknownJsonTypes() {
        for (TaskDefinition task : List.of(
                new TaskDefinition("3000000000000001", TaskDefinition.Type.INVENTORY_TAG, "#minecraft:planks", 1),
                new TaskDefinition("3000000000000001", TaskDefinition.Type.INVENTORY_TAG, "minecraft:planks", 0),
                new TaskDefinition("3000000000000001", TaskDefinition.Type.INVENTORY_TAG, "minecraft:planks", 4097),
                new Gson().fromJson("{\"id\":\"3000000000000001\",\"type\":\"UNKNOWN\",\"itemId\":\"minecraft:planks\",\"count\":1}",
                        TaskDefinition.class))) {
            assertThrows(GuideValidationException.class, () -> GuideValidator.validate(guide(task)));
        }
    }

    private static GuideDefinition guide(TaskDefinition task) {
        QuestDefinition quest = new QuestDefinition("2000000000000001", 0, "quest.test.title", "quest.test.description",
                new QuestPosition(0, 0), List.of(), List.of(task), List.of());
        return new GuideDefinition(1, "0000000000000001", "guide.test.title", "guide.test.description",
                List.of(new ChapterDefinition("1000000000000001", 0, "chapter.test.title", "chapter.test.description",
                        List.of(quest))));
    }
}
