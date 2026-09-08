package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/** Produces the client-facing chapter projection permitted by the player's observed progress. */
public final class ChapterVisibility {
    private static final Comparator<ChapterDefinition> AUTHOR_ORDER =
            Comparator.comparingInt(ChapterDefinition::order).thenComparing(ChapterDefinition::id);

    private ChapterVisibility() {}

    /** Returns the original immutable chapter definitions which the observed progress can expose. */
    public static List<ChapterDefinition> visibleChapters(GuideDefinition guide, ProgressPayload progress) {
        if (guide == null || guide.chapters() == null || guide.chapters().isEmpty()) return List.of();
        ProgressPayload observed = progress != null && progress.available() ? progress : ProgressPayload.UNAVAILABLE;
        Set<String> completedQuests = observed.state().completedQuestIds();
        Set<String> completedTasks = observed.state().completedTaskIds();
        ChapterDefinition first = guide.chapters().stream().min(AUTHOR_ORDER).orElseThrow();
        return guide.chapters().stream()
                .filter(chapter -> chapter == first || isVisible(chapter, completedQuests, completedTasks))
                .toList();
    }

    private static boolean isVisible(ChapterDefinition chapter, Set<String> completedQuests,
            Set<String> completedTasks) {
        return chapter.quests().stream().anyMatch(quest -> completedQuests.contains(quest.id())
                || quest.tasks().stream().anyMatch(task -> task.type() == TaskDefinition.Type.MANUAL
                        && completedTasks.contains(task.id()))
                || quest.prerequisitesMet(completedQuests));
    }
}
