package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.RewardDefinition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Locks the End City source lessons after gateway arrival, including their safety supplies. */
final class EndCityBatchTest {
    private static final List<String> CHORUS = List.of(
            "435FA7C29D816E3B", "7682DAF5C0B4916E", "1A8C05F7D3E692B1", "6FD15A4C283BE706");
    private static final List<String> SHULKER = List.of(
            "58A57A615D0EF444", "6FEC02D106FBA924", "4DF0AA017358AFED", "42E183B7F772D99C",
            "6C6318A949755526", "1A6EFC93E3474D60", "1239D170F79C0405", "0B875B8819CB3C72");
    private static final List<String> SHIP = List.of(
            "78C3992F3DF1AAB1", "4DA52C070AC3CC11", "36ED20D0FACD3629", "115C8A2BF37D0635",
            "7AC2CFDD99167DA0");
    private static final List<String> QUESTS = List.of(
            "435FA7C29D816E3B", "7682DAF5C0B4916E", "1A8C05F7D3E692B1", "6FD15A4C283BE706",
            "58A57A615D0EF444", "6FEC02D106FBA924", "4DF0AA017358AFED", "42E183B7F772D99C",
            "6C6318A949755526", "1A6EFC93E3474D60", "1239D170F79C0405", "0B875B8819CB3C72",
            "78C3992F3DF1AAB1", "4DA52C070AC3CC11", "36ED20D0FACD3629", "115C8A2BF37D0635",
            "7AC2CFDD99167DA0");
    private static final List<String> TASKS = List.of(
            "5460B8D3AE927F4C", "6571C9E4BFA3805D", "0793EB06D1C5A27F", "2B9D1608E4F7A3C2",
            "3CAE2719F508B4D3", "4DBF382A0619C5E4", "70E26B5D394CF817", "01F37C6E4A5D0928",
            "6F8FB5CFA3BB0D00", "02FF8330CE2C8439", "3AB38B83060F9D89", "1CC67EC6B0559BDF",
            "783D54DCD2748276", "17C988320FABF288", "73B374882251B9FB", "231A7CAB2183B609",
            "0225CACBB251A551", "1A1F8D7425CF1BBB", "383F01DB43C46BC6", "6C3CA1F2A59B96F8",
            "292681F22352C745", "2E392A3F4DA700D2", "4EDAD7D40E987575", "0C24A70B572395DF",
            "33798A78BF88632F", "6031D6DB54DCD7AF", "541A44EE25FF8D48", "17B4DEB5A8428AA8",
            "7FE2A06FEB0DF6EC", "7A79B82F2CCC4E29", "188E58CCC96170BA", "356F4D35E1E1772D",
            "2F3457D87F79CB5E", "5094B556A34332A6", "58B24B045C620E3E");

    @Test
    void preservesAllSourceIdsDependenciesTasksAndRewardsAcrossThreeChapters() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters().subList(41, 44);
        assertEquals(List.of("7EE518BD4A62F794", "7FF629CE5B7308A5", "60A73ADF6C8419B6"),
                chapters.stream().map(c -> c.id()).toList());
        assertEquals(List.of(CHORUS, SHULKER, SHIP), chapters.stream()
                .map(c -> c.quests().stream().map(q -> q.id()).toList()).toList());
        var quests = chapters.stream().flatMap(c -> c.quests().stream()).toList();
        assertEquals(List.of("213E895B26A0F5B5"), quests.getFirst().prerequisiteQuestIds());
        for (int index = 1; index < QUESTS.size(); index++) {
            assertEquals(List.of(QUESTS.get(index - 1)), quests.get(index).prerequisiteQuestIds());
        }
        var tasks = quests.stream().flatMap(q -> q.tasks().stream()).toList();
        assertEquals(TASKS, tasks.stream().map(TaskDefinition::id).toList());
        assertEquals(List.of(TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL,
                TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL,
                TaskDefinition.Type.ADVANCEMENT, TaskDefinition.Type.MANUAL,
                TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY,
                TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL, TaskDefinition.Type.MANUAL,
                TaskDefinition.Type.MANUAL, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY,
                TaskDefinition.Type.MANUAL, TaskDefinition.Type.MANUAL, TaskDefinition.Type.MANUAL,
                TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL,
                TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY,
                TaskDefinition.Type.MANUAL, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY,
                TaskDefinition.Type.MANUAL, TaskDefinition.Type.MANUAL, TaskDefinition.Type.INVENTORY,
                TaskDefinition.Type.MANUAL, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL),
                tasks.stream().map(TaskDefinition::type).toList());
        assertEquals(List.of(8, 1, 1, 64, 2, 1, 1, 1, 1, 1, 1, 32, 1, 1, 1, 32, 8, 1, 1, 1,
                2, 1, 1, 128, 2, 16, 1, 256, 16, 1, 1, 1, 1, 1, 1), tasks.stream().map(TaskDefinition::count).toList());
        var rewards = quests.stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("18A4FC17E2D6B380", "5EC0493B172AD6F5", "12048D7F5B6E1A39", "377D9627F332A842",
                "79C93CC618B04E8A", "3DCCE25084BBD9CE", "280994CA31BC6FAE"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(5, 5, 10, 10, 10, 10, 10), rewards.stream().map(r -> r.amount()).toList());
        assertTrue(rewards.stream().allMatch(r -> r.type() == RewardDefinition.Type.EXPERIENCE));
    }

    @Test
    void inventorySuppliesAreStickyAtTheirExactThresholdsWhilePracticeStaysManual() throws Exception {
        var snapshot = snapshot();
        var chorus = quest(snapshot, 0);
        var citySafety = quest(snapshot, 4);
        var shipBuild = quest(snapshot, 12);
        var shipBridge = quest(snapshot, 13);
        var flower = chorus.tasks().get(1);
        var safetyManual = citySafety.tasks().getLast();
        var low = TaskEvaluator.evaluate(snapshot, new ProgressState(Set.of(), Set.of("213E895B26A0F5B5")), key ->
                key.equals("minecraft:chorus_fruit") ? 7 : key.equals(flower.inventoryKey()) ? 0 : 0);
        assertFalse(low.completedTaskIds().contains(chorus.tasks().getFirst().id()));
        assertFalse(low.completedTaskIds().contains(flower.id()));
        var supplied = TaskEvaluator.evaluate(snapshot, low, key ->
                key.equals("minecraft:chorus_fruit") ? 8 : key.equals(flower.inventoryKey()) ? 1 : 0);
        assertTrue(supplied.completedTaskIds().containsAll(Set.of(chorus.tasks().getFirst().id(), flower.id())));
        assertEquals(supplied, TaskEvaluator.evaluate(snapshot, supplied, key -> 0));
        assertTrue(supplied.completedQuestIds().contains(chorus.id()));
        var fruitPractice = quest(snapshot, 1);
        assertFalse(supplied.completedQuestIds().contains(fruitPractice.id()));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, supplied, chorus.id(), flower.id(), key -> 1));
        assertTrue(TaskEvaluator.confirm(snapshot, supplied, fruitPractice.id(), TASKS.get(2), key -> 0)
                .completedQuestIds().contains(fruitPractice.id()));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, supplied, citySafety.id(), safetyManual.id(), key -> 0));

        assertEquals(List.of("minecraft:milk_bucket", "minecraft:water_bucket", "minecraft:shield", "minecraft:end_stone"),
                citySafety.tasks().subList(0, 4).stream().map(TaskDefinition::itemId).toList());
        assertEquals(List.of(1, 1, 1, 32), citySafety.tasks().subList(0, 4).stream().map(TaskDefinition::count).toList());
        assertEquals(List.of(128, 2, 16), shipBuild.tasks().subList(0, 3).stream().map(TaskDefinition::count).toList());
        assertEquals(List.of(256, 16), shipBridge.tasks().subList(0, 2).stream().map(TaskDefinition::count).toList());
        assertEquals("minecraft:shulker_shell", quest(snapshot, 10).tasks().getFirst().itemId());
        assertEquals(2, quest(snapshot, 10).tasks().getFirst().count());
        assertEquals("minecraft:elytra", quest(snapshot, 15).tasks().getFirst().itemId());
    }

    @Test
    void endCityAchievementIsAutomaticAndCannotBypassItsManualRoute() throws Exception {
        var snapshot = snapshot();
        var city = quest(snapshot, 3);
        var achievement = city.tasks().getFirst();
        assertEquals(TaskDefinition.Type.ADVANCEMENT, achievement.type());
        assertEquals("minecraft:end/find_end_city", achievement.advancementId());
        assertEquals("in_city", achievement.criterion());
        var observed = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY,
                key -> key.equals(achievement.inventoryKey()) ? 1 : 0);
        assertTrue(observed.completedTaskIds().contains(achievement.id()));
        assertFalse(observed.completedQuestIds().contains(city.id()));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, observed, city.id(), achievement.id(), key -> 0));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, observed, city.id(), city.tasks().get(1).id(), key -> 0));
        var routed = new ProgressState(observed.completedTaskIds(), Set.of(QUESTS.get(2)));
        var finished = TaskEvaluator.confirm(snapshot, routed, city.id(), city.tasks().get(1).id(), key -> 0);
        assertTrue(finished.completedQuestIds().contains(city.id()));
    }

    private static ch.minenox.firsttorch.guide.model.QuestDefinition quest(GuideSnapshot snapshot, int index) {
        return snapshot.guides().getFirst().chapters().subList(41, 44).stream()
                .flatMap(chapter -> chapter.quests().stream()).toList().get(index);
    }

    private static GuideSnapshot snapshot() throws Exception {
        try (var input = EndCityBatchTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
