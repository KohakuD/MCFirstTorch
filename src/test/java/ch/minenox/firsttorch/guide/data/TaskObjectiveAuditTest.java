package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Guards the source objective split: possessions are automatic; practice remains explicit. */
final class TaskObjectiveAuditTest {
    private static final Map<String, List<TaskDefinition.Type>> MIXED_PRACTICE = Map.of(
            "46B7D91E2A5C803F", List.of(TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL),
            "65A1B2C3D4E5F607", List.of(TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL),
            "6A24D8F30C715BE9", List.of(TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL));

    @Test
    void everyCourseTaskHasAStableExplicitBilingualTitle() throws Exception {
        GuideDefinition course = course();
        var tasks = course.chapters().stream().flatMap(chapter -> chapter.quests().stream())
                .flatMap(quest -> quest.tasks().stream()).toList();
        assertEquals(207, tasks.size());
        assertEquals(207, tasks.stream().map(TaskDefinition::id).distinct().count());
        for (TaskDefinition task : tasks) {
            assertEquals("task.firsttorch." + task.id().toLowerCase(java.util.Locale.ROOT) + ".title", task.titleKey());
            for (String locale : List.of("en_us", "de_de")) assertFalse(translations(locale).get(task.titleKey()).getAsString().isBlank());
        }
    }

    @Test
    void sourceItemObjectivesStayAutomaticAndPracticeObjectivesStayManual() throws Exception {
        GuideDefinition course = course();
        GuideDefinition alpha = guide("getting_started");
        GuideSnapshot snapshot = new GuideSnapshot(List.of(course, alpha));
        var quests = List.of(course, alpha).stream().flatMap(guide -> guide.chapters().stream())
                .flatMap(chapter -> chapter.quests().stream()).toList();
        assertEquals(177, quests.size());
        assertEquals(209, quests.stream().mapToInt(quest -> quest.tasks().size()).sum());
        for (var quest : quests) {
            for (TaskDefinition task : quest.tasks()) {
                if (task.automatic()) {
                    assertThrows(IllegalArgumentException.class,
                            () -> TaskEvaluator.confirm(snapshot, ProgressState.EMPTY, quest.id(), task.id(), key -> 4096),
                            "item task must not accept manual confirmation: " + task.id());
                }
            }
        }
        assertEquals(Set.of("46B7D91E2A5C803F", "65A1B2C3D4E5F607", "6A24D8F30C715BE9"), MIXED_PRACTICE.keySet());
        for (var quest : quests) {
            List<TaskDefinition.Type> expected = MIXED_PRACTICE.get(quest.id());
            if (expected != null) assertEquals(expected, quest.tasks().stream().map(TaskDefinition::type).toList());
        }
    }

    private static GuideDefinition course() throws Exception {
        return guide("course");
    }

    private static GuideDefinition guide(String name) throws Exception {
        try (var input = TaskObjectiveAuditTest.class.getResourceAsStream("/data/firsttorch/guides/" + name + ".json")) {
            return GuideJson.read(input);
        }
    }

    private static com.google.gson.JsonObject translations(String locale) throws Exception {
        try (var input = TaskObjectiveAuditTest.class.getResourceAsStream("/assets/firsttorch/lang/" + locale + ".json")) {
            return JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }
}
