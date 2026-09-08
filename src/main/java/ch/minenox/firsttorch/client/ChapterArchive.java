package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Presentation-only grouping; the full visible chapter list remains searchable. */
final class ChapterArchive {
    record Row(ChapterDefinition chapter, int completedCount) {
        boolean heading() { return chapter == null; }
    }

    static boolean completed(ChapterDefinition chapter, Predicate<String> completedQuest) {
        return !chapter.quests().isEmpty() && chapter.quests().stream().allMatch(q -> completedQuest.test(q.id()));
    }

    static List<Row> rows(List<ChapterDefinition> chapters, Predicate<String> completedQuest, boolean expanded) {
        List<Row> rows = new ArrayList<>();
        List<ChapterDefinition> finished = new ArrayList<>();
        for (var chapter : chapters) {
            if (completed(chapter, completedQuest)) finished.add(chapter);
            else rows.add(new Row(chapter, 0));
        }
        if (!finished.isEmpty()) {
            List<Row> archive = new ArrayList<>();
            archive.add(new Row(null, finished.size()));
            if (expanded) finished.forEach(c -> archive.add(new Row(c, 0)));
            rows.addAll(0, archive);
        }
        return List.copyOf(rows);
    }
}
