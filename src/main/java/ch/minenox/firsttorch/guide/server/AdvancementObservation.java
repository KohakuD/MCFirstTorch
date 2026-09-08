package ch.minenox.firsttorch.guide.server;

import net.minecraft.advancements.AdvancementProgress;

/** Reads existing vanilla progress; never awards or revokes an advancement. */
final class AdvancementObservation {
    static int count(AdvancementProgress progress, String criterion) {
        if (progress == null) return 0;
        if (criterion == null) return progress.isDone() ? 1 : 0;
        var result = progress.getCriterion(criterion);
        return result != null && result.isDone() ? 1 : 0;
    }
    private AdvancementObservation() {}
}
