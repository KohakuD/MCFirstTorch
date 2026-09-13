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
    @Test void cumulativeHistoryIncludesOlderRevisionsAndNewSulfurCards() throws Exception {
        try (var target = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var snapshot = new GuideSnapshot(List.of(GuideJson.read(target)));
            var history = ch.minenox.firsttorch.guide.edition.FirstTorchEditionHistory.create();
            var recent = history.compare("26.1.2", "26.2", snapshot);
            assertEquals(Set.of("32B4C6D8E0F21357", "2A26200000000001", "2A26200000000002",
                    "2A26200000000003", "2A26200000000004", "2A26200000000005"), recent.changedQuestIds());
            var cumulative = history.compare("1.21.1", "26.2", snapshot);
            assertEquals(51, cumulative.changedQuestIds().size());
            assertTrue(cumulative.changedQuestIds().containsAll(recent.changedQuestIds()));
            assertTrue(cumulative.changedQuestIds().contains("59CBED086F24A137"));
            assertFalse(cumulative.changedQuestIds().contains("6D91380C6EA4B2F5"));
            assertTrue(cumulative.contextQuestIds().contains("3C3122DF5C0EA192"));
        }
    }

    @Test void preservesExistingCourseAndAddsOnlyOptionalReadingCards() throws Exception {
        try (var source = Files.newInputStream(Path.of(System.getProperty("firsttorch.projectDir"),
                "src/main/resources/data/firsttorch/guides/course.json"));
             var target = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var baseline = GuideJson.read(source);
            var edition = GuideJson.read(target);
            var shared = edition.chapters().stream().filter(c -> !c.id().equals("7A26200000000001")).toList();
            assertEquals(baseline.chapters().stream().map(c -> c.id()).toList(), shared.stream().map(c -> c.id()).toList());
            for (int i = 0; i < shared.size(); i++) {
                assertEquals(baseline.chapters().get(i).quests(), shared.get(i).quests());
                assertEquals(baseline.chapters().get(i).titleKey(), shared.get(i).titleKey());
                assertEquals(baseline.chapters().get(i).descriptionKey(), shared.get(i).descriptionKey());
                assertEquals(baseline.chapters().get(i).iconItemId(), shared.get(i).iconItemId());
            }
            assertDoesNotThrow(() -> new GuideSnapshot(List.of(edition)));
            var chapter = edition.chapters().stream().filter(c -> c.id().equals("7A26200000000001")).findFirst().orElseThrow();
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
