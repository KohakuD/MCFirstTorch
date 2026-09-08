package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public record GuideBrowserViewModel(
        List<GuideDefinition> guides,
        @Nullable GuideDefinition guide,
        List<ChapterDefinition> chapters,
        @Nullable ChapterDefinition chapter,
        List<QuestDefinition> quests,
        @Nullable QuestDefinition quest,
        List<Prerequisite> prerequisites,
        Selection selection) {

    private static final Comparator<ChapterDefinition> CHAPTER_ORDER =
            Comparator.comparingInt(ChapterDefinition::order).thenComparing(ChapterDefinition::id);
    private static final Comparator<QuestDefinition> QUEST_ORDER =
            Comparator.comparingInt(QuestDefinition::order).thenComparing(QuestDefinition::id);

    public GuideBrowserViewModel {
        guides = List.copyOf(guides);
        chapters = List.copyOf(chapters);
        quests = List.copyOf(quests);
        prerequisites = List.copyOf(prerequisites);
    }

    public static GuideBrowserViewModel resolve(GuideSnapshot snapshot, Selection preferred) {
        return resolve(snapshot, preferred, GuideDefinition::chapters);
    }

    public static GuideBrowserViewModel resolve(GuideSnapshot snapshot, Selection preferred, ProgressPayload progress) {
        return resolve(snapshot, preferred, guide -> ChapterVisibility.visibleChapters(guide, progress));
    }

    private static GuideBrowserViewModel resolve(GuideSnapshot snapshot, Selection preferred,
            java.util.function.Function<GuideDefinition, List<ChapterDefinition>> visibleChapters) {
        List<GuideDefinition> guides = snapshot.guides();
        if (guides.isEmpty()) {
            return new GuideBrowserViewModel(
                    List.of(), null, List.of(), null, List.of(), null, List.of(), Selection.EMPTY);
        }

        GuideDefinition guide = findGuide(guides, preferred.guideId());
        List<ChapterDefinition> chapters = visibleChapters.apply(guide).stream().sorted(CHAPTER_ORDER).toList();
        ChapterDefinition chapter = findChapter(chapters, preferred.chapterId());
        List<QuestDefinition> quests = chapter.quests().stream().sorted(QUEST_ORDER).toList();
        QuestDefinition quest = findQuest(quests, preferred.questId());

        Map<String, QuestDefinition> questsById = new LinkedHashMap<>();
        for (ChapterDefinition candidateChapter : guide.chapters()) {
            for (QuestDefinition candidateQuest : candidateChapter.quests()) {
                questsById.put(candidateQuest.id(), candidateQuest);
            }
        }
        List<Prerequisite> prerequisites = new ArrayList<>();
        for (String prerequisiteId : quest.prerequisiteQuestIds()) {
            QuestDefinition prerequisite = questsById.get(prerequisiteId);
            if (prerequisite != null) {
                prerequisites.add(new Prerequisite(prerequisite.id(), prerequisite.titleKey()));
            }
        }

        Selection resolved = new Selection(guide.id(), chapter.id(), quest.id());
        return new GuideBrowserViewModel(
                guides, guide, chapters, chapter, quests, quest, prerequisites, resolved);
    }

    public int guideIndex() {
        return guide == null ? -1 : guides.indexOf(guide);
    }

    private static GuideDefinition findGuide(List<GuideDefinition> guides, @Nullable String id) {
        return guides.stream().filter(guide -> guide.id().equals(id)).findFirst().orElse(guides.getFirst());
    }

    private static ChapterDefinition findChapter(List<ChapterDefinition> chapters, @Nullable String id) {
        return chapters.stream().filter(chapter -> chapter.id().equals(id)).findFirst().orElse(chapters.getFirst());
    }

    private static QuestDefinition findQuest(List<QuestDefinition> quests, @Nullable String id) {
        return quests.stream().filter(quest -> quest.id().equals(id)).findFirst().orElse(quests.getFirst());
    }

    public record Selection(
            @Nullable String guideId,
            @Nullable String chapterId,
            @Nullable String questId) {
        public static final Selection EMPTY = new Selection(null, null, null);
    }

    public record Prerequisite(String questId, String titleKey) {
    }
}
