package ch.minenox.firsttorch.guide.server;

import static org.junit.jupiter.api.Assertions.*;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementRequirements;
import java.util.List;
import org.junit.jupiter.api.Test;

final class AdvancementObservationTest {
    @Test void exactCriterionIsIndependentOfWholeAdvancementCompletion() {
        var progress = new AdvancementProgress();
        progress.update(new AdvancementRequirements(List.of(List.of("slept_in_bed"), List.of("other"))));
        assertEquals(0, AdvancementObservation.count(progress, "slept_in_bed"));
        progress.grantProgress("other");
        assertEquals(0, AdvancementObservation.count(progress, "slept_in_bed"));
        progress.revokeProgress("other");
        progress.grantProgress("slept_in_bed");
        assertEquals(1, AdvancementObservation.count(progress, "slept_in_bed"));
        assertEquals(0, AdvancementObservation.count(progress, null));
        assertEquals(0, AdvancementObservation.count(progress, "unknown"));
        assertEquals(0, AdvancementObservation.count(null, "slept_in_bed"));
        progress.grantProgress("other");
        assertEquals(1, AdvancementObservation.count(progress, null));
    }
}
