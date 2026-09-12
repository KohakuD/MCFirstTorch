package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.minenox.firsttorch.client.GuideBrowserViewModel.Selection;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import java.util.List;
import org.junit.jupiter.api.Test;

final class GuideBrowserViewModelTest {
    private static final String GUIDE_ID = "0000000000000001";
    private static final String CHAPTER_A_ID = "1000000000000001";
    private static final String CHAPTER_B_ID = "1000000000000002";
    private static final String QUEST_A_ID = "2000000000000001";
    private static final String QUEST_B_ID = "2000000000000002";

    @Test
    void ordersChaptersAndQuestsByExplicitOrder() {
        ChapterDefinition later = chapter(CHAPTER_B_ID, 5, quest(QUEST_B_ID, 3, List.of()));
        ChapterDefinition earlier = chapter(CHAPTER_A_ID, 1, quest(QUEST_A_ID, 2, List.of()));
        GuideSnapshot snapshot = new GuideSnapshot(List.of(guide(List.of(later, earlier))));

        GuideBrowserViewModel view = GuideBrowserViewModel.resolve(snapshot, Selection.EMPTY);

        assertEquals(List.of(CHAPTER_A_ID, CHAPTER_B_ID), view.chapters().stream().map(ChapterDefinition::id).toList());
        assertEquals(CHAPTER_A_ID, view.chapter().id());
        assertEquals(QUEST_A_ID, view.quest().id());
    }

    @Test
    void preservesStableSelectionAcrossSnapshotReplacement() {
        GuideSnapshot first = twoQuestSnapshot();
        Selection selected = new Selection(GUIDE_ID, CHAPTER_A_ID, QUEST_B_ID);
        GuideBrowserViewModel initial = GuideBrowserViewModel.resolve(first, selected);
        GuideSnapshot replacement = twoQuestSnapshot();

        GuideBrowserViewModel reloaded = GuideBrowserViewModel.resolve(replacement, initial.selection());

        assertEquals(selected, reloaded.selection());
    }

    @Test
    void fallsBackDeterministicallyWhenSelectionDisappears() {
        GuideSnapshot snapshot = new GuideSnapshot(List.of(guide(List.of(chapter(
                CHAPTER_A_ID,
                0,
                quest(QUEST_A_ID, 0, List.of()))))));

        GuideBrowserViewModel view = GuideBrowserViewModel.resolve(
                snapshot,
                new Selection("0000000000000099", "1000000000000099", "2000000000000099"));

        assertEquals(new Selection(GUIDE_ID, CHAPTER_A_ID, QUEST_A_ID), view.selection());
    }

    @Test
    void exposesPrerequisiteTitlesInDeclaredOrder() {
        GuideBrowserViewModel view = GuideBrowserViewModel.resolve(
                twoQuestSnapshot(),
                new Selection(GUIDE_ID, CHAPTER_A_ID, QUEST_B_ID));

        assertEquals(1, view.prerequisites().size());
        assertEquals(QUEST_A_ID, view.prerequisites().getFirst().questId());
        assertEquals("quest.test." + QUEST_A_ID + ".title", view.prerequisites().getFirst().titleKey());
    }

    @Test
    void selectsMultipleGuidesInSnapshotOrder() {
        GuideDefinition first = guide(List.of(chapter(
                CHAPTER_A_ID, 0, quest(QUEST_A_ID, 0, List.of()))));
        GuideDefinition second = new GuideDefinition(
                1,
                "0000000000000002",
                "guide.test.second.title",
                "guide.test.second.description",
                List.of(chapter("1000000000000003", 0, quest("2000000000000003", 0, List.of()))));
        GuideSnapshot snapshot = new GuideSnapshot(List.of(first, second));

        GuideBrowserViewModel view = GuideBrowserViewModel.resolve(
                snapshot, new Selection(second.id(), null, null));

        assertEquals(second.id(), view.guide().id());
        assertEquals(1, view.guideIndex());
        assertEquals(List.of(first.id(), second.id()), view.guides().stream().map(GuideDefinition::id).toList());
    }
    @Test
    void visibilityDoesNotRemoveHiddenPrerequisiteDefinitionsOrLeakHiddenSelection() {
        String chapterC = "1000000000000003", questC = "2000000000000003";
        var first = chapter(CHAPTER_A_ID, 0, quest(QUEST_A_ID, 0, List.of()));
        var hidden = chapter(CHAPTER_B_ID, 1, quest(QUEST_B_ID, 0, List.of(QUEST_A_ID)));
        var started = chapter(chapterC, 2, quest(questC, 0, List.of(QUEST_B_ID)));
        var snapshot = new GuideSnapshot(List.of(guide(List.of(first, hidden, started))));
        var progress = new ch.minenox.firsttorch.network.ProgressPayload(
                new ch.minenox.firsttorch.guide.progress.ProgressState(java.util.Set.of(), java.util.Set.of(questC)),
                java.util.Map.of(), true);
        var view = GuideBrowserViewModel.resolve(snapshot, new Selection(GUIDE_ID, chapterC, questC), progress);
        assertEquals(List.of(CHAPTER_A_ID, chapterC), view.chapters().stream().map(ChapterDefinition::id).toList());
        assertEquals(3, view.guide().chapters().size());
        assertEquals(QUEST_B_ID, view.prerequisites().getFirst().questId());
        var rejected = GuideBrowserViewModel.resolve(snapshot, new Selection(GUIDE_ID, CHAPTER_B_ID, QUEST_B_ID), progress);
        assertEquals(CHAPTER_A_ID, rejected.chapter().id());
        assertEquals(3, GuideBrowserViewModel.resolve(snapshot, Selection.EMPTY).chapters().size());
    }

    @Test
    void comparisonKeepsLockedLessonReadableAndOriginalPrerequisitesIntact() {
        var snapshot = twoQuestSnapshot();
        var view = GuideBrowserViewModel.resolveComparison(snapshot, Selection.EMPTY, java.util.Set.of(QUEST_B_ID));
        assertEquals(List.of(QUEST_B_ID), view.quests().stream().map(QuestDefinition::id).toList());
        assertEquals(QUEST_A_ID, view.prerequisites().getFirst().questId());
        assertEquals(false, view.quest().prerequisitesMet(id -> false));
        assertEquals(2, snapshot.guides().getFirst().chapters().getFirst().quests().size());
        assertEquals(2, GuideBrowserViewModel.resolve(snapshot, Selection.EMPTY).quests().size());
    }

    @Test
    void comparisonOmitsEmptyChaptersAndRejectsHiddenSelection() {
        var snapshot = new GuideSnapshot(List.of(guide(List.of(
                chapter(CHAPTER_A_ID, 0, quest(QUEST_A_ID, 0, List.of())),
                chapter(CHAPTER_B_ID, 1, quest(QUEST_B_ID, 0, List.of(QUEST_A_ID)))))));
        var view = GuideBrowserViewModel.resolveComparison(snapshot,
                new Selection(GUIDE_ID, CHAPTER_A_ID, QUEST_A_ID), java.util.Set.of(QUEST_B_ID));
        assertEquals(List.of(CHAPTER_B_ID), view.chapters().stream().map(ChapterDefinition::id).toList());
        assertEquals(QUEST_B_ID, view.quest().id());
        var empty = GuideBrowserViewModel.resolveComparison(snapshot, view.selection(), java.util.Set.of());
        assertEquals(null, empty.guide());
        assertEquals(Selection.EMPTY, empty.selection());
    }

    private static GuideSnapshot twoQuestSnapshot() {
        QuestDefinition prerequisite = quest(QUEST_A_ID, 0, List.of());
        QuestDefinition dependent = quest(QUEST_B_ID, 1, List.of(QUEST_A_ID));
        return new GuideSnapshot(List.of(guide(List.of(chapter(
                CHAPTER_A_ID, 0, prerequisite, dependent)))));
    }

    private static GuideDefinition guide(List<ChapterDefinition> chapters) {
        return new GuideDefinition(
                1, GUIDE_ID, "guide.test.title", "guide.test.description", chapters);
    }

    private static ChapterDefinition chapter(String id, int order, QuestDefinition... quests) {
        return new ChapterDefinition(
                id, order, "chapter.test." + id + ".title", "chapter.test." + id + ".description", List.of(quests));
    }

    private static QuestDefinition quest(String id, int order, List<String> prerequisites) {
        return new QuestDefinition(
                id,
                order,
                "quest.test." + id + ".title",
                "quest.test." + id + ".description",
                new QuestPosition(order * 2, 0),
                prerequisites);
    }
}
