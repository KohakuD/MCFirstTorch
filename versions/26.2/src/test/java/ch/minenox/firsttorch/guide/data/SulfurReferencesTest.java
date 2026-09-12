package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

final class SulfurReferencesTest {
    @Test void preservesExistingCourseAndAddsOnlyOptionalReadingCards() throws Exception {
        try (var source = Files.newInputStream(Path.of(System.getProperty("firsttorch.projectDir"),
                "src/main/resources/data/firsttorch/guides/course.json"));
             var target = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var baseline = GuideJson.read(source);
            var edition = GuideJson.read(target);
            assertEquals(baseline.chapters(), edition.chapters().subList(0, baseline.chapters().size()));
            assertDoesNotThrow(() -> new GuideSnapshot(List.of(edition)));
            var chapter = edition.chapters().getLast();
            assertEquals("7A26200000000001", chapter.id());
            assertEquals(5, chapter.quests().size());
            var added = chapter.quests().stream().map(q -> q.id()).collect(Collectors.toSet());
            for (var old : baseline.chapters()) for (var quest : old.quests())
                assertTrue(quest.prerequisiteQuestIds().stream().noneMatch(added::contains));
            for (var quest : chapter.quests()) {
                assertEquals(List.of("3C3122DF5C0EA192"), quest.prerequisiteQuestIds());
                assertTrue(quest.rewards().isEmpty());
                assertEquals(1, quest.tasks().size());
                assertEquals(TaskDefinition.Type.MANUAL, quest.tasks().getFirst().type());
                assertNotEquals(Items.AIR, BuiltInRegistries.ITEM.getValue(Identifier.parse(quest.iconItemId())));
            }
        }
    }
}
