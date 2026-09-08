package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.guide.data.GuideJson;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class ProtectionAlternativePrerequisitesVisibilityTest {
    private static final String PROTECTION_CHAPTER = "4C86EA032D9F517B";
    private static final String IRON_INGOT = "3C16B9E50A724DF8";
    private static final String COPPER_INGOT = "6C4AE8F31D957B20";
    private static final String SHIELD = "4D92C7A10E638BF5";
    private static final String ARMOUR = "18A6D3F90C754BE2";

    @Test
    void copperRouteExposesProtectionThroughArmourButNotShield() throws Exception {
        GuideDefinition guide = course();
        QuestDefinition shield = quest(guide, SHIELD);
        QuestDefinition armour = quest(guide, ARMOUR);

        assertTrue(ChapterVisibility.visibleChapters(guide, progress(COPPER_INGOT)).stream()
                .map(ChapterDefinition::id).anyMatch(PROTECTION_CHAPTER::equals));
        assertTrue(armour.prerequisitesMet(Set.of(COPPER_INGOT)));
        assertFalse(shield.prerequisitesMet(Set.of(COPPER_INGOT)));
    }

    @Test
    void ironRouteUnlocksArmourWithoutCopperAndNeitherRouteKeepsItLocked() throws Exception {
        GuideDefinition guide = course();
        QuestDefinition armour = quest(guide, ARMOUR);

        assertTrue(armour.prerequisitesMet(Set.of(IRON_INGOT)));
        assertFalse(armour.prerequisitesMet(Set.of()));
        assertFalse(ChapterVisibility.visibleChapters(guide, progress()).stream()
                .map(ChapterDefinition::id).anyMatch(PROTECTION_CHAPTER::equals));
    }

    private static GuideDefinition course() throws Exception {
        try (InputStream input = ProtectionAlternativePrerequisitesVisibilityTest.class
                .getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return GuideJson.read(input);
        }
    }

    private static QuestDefinition quest(GuideDefinition guide, String id) {
        return guide.chapters().stream().flatMap(chapter -> chapter.quests().stream())
                .filter(quest -> quest.id().equals(id)).findFirst().orElseThrow();
    }

    private static ProgressPayload progress(String... completed) {
        return new ProgressPayload(new ProgressState(Set.of(), Set.of(completed)), Map.of(), true);
    }
}
