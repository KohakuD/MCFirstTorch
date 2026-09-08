package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class WelcomeCourseTest {
    private static final List<String> QUESTS = List.of("12E6801F4CAD3579", "21A3B5C7D9E10246",
            "32B4C6D8E0F21357", "43C5D7E9F1023468", "54D6E8F102345679", "65E7F1023456780A",
            "06F8123456789A2B", "17A923456789AB3C");
    private static final List<String> TASKS = List.of("23F7912A5DBE4680", "0A12B34C56D78E90",
            "1B23C45D67E89F01", "2C34D56E78F90112", "3D45E67F89012324", "4E56F78012345635",
            "5F67012345678946", "6078123456789A57");

    @Test
    void preservesSourceIdentifiersDependenciesAndSmallReward() throws Exception {
        var guide = load("course");
        assertEquals("0F91A2B3C4D5E607", guide.chapters().getFirst().id());
        var quests = guide.chapters().getFirst().quests();
        assertEquals(QUESTS, quests.stream().map(QuestDefinition::id).toList());
        assertEquals(TASKS, quests.stream().map(q -> q.tasks().getFirst().id()).toList());
        assertTrue(quests.getFirst().prerequisiteQuestIds().isEmpty());
        for (int i = 1; i < 7; i++) assertEquals(List.of(QUESTS.getFirst()), quests.get(i).prerequisiteQuestIds());
        assertEquals(Set.copyOf(QUESTS.subList(1, 7)), Set.copyOf(quests.getLast().prerequisiteQuestIds()));
        for (var quest : quests) {
            assertEquals(1, quest.tasks().size());
            assertEquals(TaskDefinition.Type.MANUAL, quest.tasks().getFirst().type());
        }
        var rewards = quests.stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(1, rewards.size());
        assertEquals("4BD9A6E31F028C75", rewards.getFirst().id());
        assertEquals(ch.minenox.firsttorch.guide.model.RewardDefinition.Type.EXPERIENCE, rewards.getFirst().type());
        assertEquals(5, rewards.getFirst().amount());
        new GuideSnapshot(List.of(guide, load("getting_started")));
    }

    @Test
    void requiresAllIntroductionBranchesAndRetainsAlphaProgress() throws Exception {
        var snapshot = new GuideSnapshot(List.of(load("course"), load("getting_started")));
        var alpha = new ProgressState(Set.of("3A13F17C00000001", "3A13F17C00000002"),
                Set.of("2A13F17C00000001", "2A13F17C00000002"));
        assertEquals(alpha, TaskEvaluator.evaluate(snapshot, alpha, item -> 0));
        var state = alpha;
        for (int i = 0; i < 7; i++) {
            var before = state;
            assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(
                    snapshot, before, QUESTS.getLast(), TASKS.getLast(), item -> 0));
            state = TaskEvaluator.confirm(snapshot, state, QUESTS.get(i), TASKS.get(i), item -> 0);
        }
        state = TaskEvaluator.confirm(snapshot, state, QUESTS.getLast(), TASKS.getLast(), item -> 0);
        assertEquals(10, state.completedQuestIds().size());
        assertTrue(state.completedQuestIds().containsAll(alpha.completedQuestIds()));
    }

    @Test
    void firstStepsKeepsSourceIdsAndRewardsAndRequiresWelcome() throws Exception {
        var guide = load("course");
        assertEquals(55, guide.chapters().size());
        var chapter = guide.chapters().get(1);
        assertEquals("01F57C0E3B9D2468", chapter.id());
        var quests = chapter.quests().subList(0, 5);
        assertEquals(List.of("34A8023B6ECF5791", "0A71D4C8E2F963B5", "1B82E5D9F30A74C6",
                "2C93F6EA041B85D7", "3DA407FB152C96E8"), quests.stream().map(QuestDefinition::id).toList());
        assertEquals(List.of("6A72C4E819B305FD", "4EB5180C263DA7F9", "5FC6291D374EB80A",
                "60D73A2E485FC91B", "71E84B3F5960DA2C"),
                quests.stream().map(q -> q.tasks().getFirst().id()).toList());
        assertEquals(List.of(QUESTS.getLast()), quests.getFirst().prerequisiteQuestIds());
        var snapshot = new GuideSnapshot(List.of(guide));
        for (var quest : quests) {
            assertEquals(1, quest.tasks().size());
            assertEquals(TaskDefinition.Type.MANUAL, quest.tasks().getFirst().type());
            assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot,
                    ProgressState.EMPTY, quest.id(), quest.tasks().getFirst().id(), item -> 0));
        }
        var state = new ProgressState(Set.copyOf(TASKS), Set.copyOf(QUESTS));
        for (int i = 0; i < quests.size(); i++) {
            var quest = quests.get(i);
            if (i > 0) assertEquals(List.of(quests.get(i - 1).id()), quest.prerequisiteQuestIds());
            state = TaskEvaluator.confirm(snapshot, state, quest.id(), quest.tasks().getFirst().id(), item -> 0);
        }
        assertEquals(13, state.completedQuestIds().size());
        var rewards = quests.stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("2C8E51A70D4B639F", "16C2A8E4F90B735D", "27D3B9F50A1C846E"),
                rewards.stream().map(ch.minenox.firsttorch.guide.model.RewardDefinition::id).toList());
        assertEquals(List.of(2, 2, 5), rewards.stream()
                .map(ch.minenox.firsttorch.guide.model.RewardDefinition::amount).toList());
        assertEquals("minecraft:apple", rewards.get(0).itemId());
        assertEquals("minecraft:apple", rewards.get(1).itemId());
        assertEquals(ch.minenox.firsttorch.guide.model.RewardDefinition.Type.EXPERIENCE, rewards.get(2).type());
    }

    @Test
    void translatesEveryCourseEntryInBothLanguages() throws Exception {
        var guide = load("course");
        var keys = new java.util.ArrayList<>(List.of(guide.titleKey(), guide.descriptionKey()));
        for (var chapter : guide.chapters()) {
            keys.addAll(List.of(chapter.titleKey(), chapter.descriptionKey()));
            for (var quest : chapter.quests()) keys.addAll(List.of(quest.titleKey(), quest.descriptionKey()));
        }
        for (String locale : List.of("en_us", "de_de")) {
            try (var input = getClass().getResourceAsStream("/assets/firsttorch/lang/" + locale + ".json")) {
                assertNotNull(input);
                var translations = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
                for (String key : keys) {
                    assertTrue(translations.has(key), locale + ": " + key);
                    assertFalse(translations.get(key).getAsString().isBlank());
                }
            }
        }
    }

    private static GuideDefinition load(String name) throws Exception {
        try (var input = WelcomeCourseTest.class.getResourceAsStream("/data/firsttorch/guides/" + name + ".json")) {
            assertNotNull(input);
            return GuideJson.read(input);
        }
    }
}
