package ch.minenox.firsttorch.guide.progress;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.data.GuideJson;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Verifies the server's pure completion gate that RewardClaims requires before a payout. */
final class ProtectionAlternativePrerequisitesRewardGateTest {
    private static final String IRON_INGOT = "3C16B9E50A724DF8";
    private static final String COPPER_INGOT = "6C4AE8F31D957B20";
    private static final String ARMOUR = "18A6D3F90C754BE2";
    private static final String ARMOUR_TASK = "29B7E4C10D836AF5";

    @Test
    void armourRewardCannotBeReachedBeforeEitherAlternativeRouteCompletes() throws Exception {
        GuideSnapshot snapshot = course();
        QuestDefinition armour = armour(snapshot);

        assertFalse(armour.rewards().isEmpty());
        ProgressState evaluated = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, ignored -> 0);
        assertFalse(evaluated.completedQuestIds().contains(ARMOUR));
        assertThrows(IllegalArgumentException.class,
                () -> TaskEvaluator.confirm(snapshot, evaluated, ARMOUR, ARMOUR_TASK, ignored -> 0));
    }

    @Test
    void eitherCompletedRouteAllowsTheArmourManualStepAndItsRewardGate() throws Exception {
        GuideSnapshot snapshot = course();

        ProgressState copper = new ProgressState(Set.of(), Set.of(COPPER_INGOT));
        ProgressState iron = new ProgressState(Set.of(), Set.of(IRON_INGOT));

        assertDoesNotThrow(() -> TaskEvaluator.confirm(snapshot, copper, ARMOUR, ARMOUR_TASK, ignored -> 0));
        assertDoesNotThrow(() -> TaskEvaluator.confirm(snapshot, iron, ARMOUR, ARMOUR_TASK, ignored -> 0));
        assertTrue(TaskEvaluator.confirm(snapshot, copper, ARMOUR, ARMOUR_TASK, ignored -> 0)
                .completedQuestIds().contains(ARMOUR));
        assertTrue(TaskEvaluator.confirm(snapshot, iron, ARMOUR, ARMOUR_TASK, ignored -> 0)
                .completedQuestIds().contains(ARMOUR));
    }

    private static GuideSnapshot course() throws Exception {
        try (InputStream input = ProtectionAlternativePrerequisitesRewardGateTest.class
                .getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }

    private static QuestDefinition armour(GuideSnapshot snapshot) {
        return snapshot.guides().stream().flatMap(guide -> guide.chapters().stream())
                .flatMap(chapter -> chapter.quests().stream()).filter(quest -> quest.id().equals(ARMOUR))
                .findFirst().orElseThrow();
    }
}
