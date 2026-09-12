package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.edition.FirstTorchEditionHistory;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

class TinyTakeoverReferencesTest {
    @Test void lessonsBelongTo261AndNeverBecome262OnlyInnovations() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var guide = GuideJson.read(input);
            var chapter = guide.chapters().stream().filter(c -> c.id().equals("7A26100000000001"))
                    .findFirst().orElseThrow();
            assertEquals(4, chapter.quests().size());
            var ids = chapter.quests().stream().map(q -> q.id()).collect(Collectors.toSet());
            var snapshot = new GuideSnapshot(List.of(guide));
            var history = FirstTorchEditionHistory.create();
            assertTrue(history.compare("1.21.1", "26.1.2", snapshot).changedQuestIds().containsAll(ids));
            assertTrue(history.compare("1.21.11", "26.1", snapshot).changedQuestIds().containsAll(ids));
            assertTrue(history.compare("26.1.2", "26.2", snapshot).changedQuestIds().stream().noneMatch(ids::contains));
            assertEquals(List.of("1.21.1", "26.1.2"), history.baselines("26.2", Set.of("1.21.1", "26.1.2")));
            for (var quest : chapter.quests()) {
                assertEquals(List.of("3C3122DF5C0EA192"), quest.prerequisiteQuestIds());
                assertEquals(TaskDefinition.Type.MANUAL, quest.tasks().getFirst().type());
                assertTrue(quest.rewards().isEmpty());
                assertNotEquals(Items.AIR, BuiltInRegistries.ITEM.getValue(Identifier.parse(quest.iconItemId())));
            }
        }
    }
}
