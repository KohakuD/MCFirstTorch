package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/** Opening suggestion for the bundled curriculum, without changing prerequisites or user selection mid-read. */
final class QuestRecommendation {
    // Recommendation priorities only: parallel endpoints never add prerequisite gates.
    private static final String COURSE = "0013F17C00000001";
    private static final String COURSE_GOAL = "0BED012A8146C359";
    private static final String IRON_GOAL = "3F215C6E03ABD479";

    static GuideBrowserViewModel.Selection choose(GuideSnapshot snapshot, ProgressPayload progress) {
        if (snapshot.guides().isEmpty() || progress == null || !progress.available()) return GuideBrowserViewModel.Selection.EMPTY;
        var guide = snapshot.guides().stream().filter(g -> g.id().equals(COURSE)).findFirst().orElse(snapshot.guides().getFirst());
        var chapters = guide.chapters().stream().sorted(Comparator.comparingInt(ChapterDefinition::order).thenComparing(ChapterDefinition::id)).toList();
        var quests = chapters.stream().flatMap(c -> c.quests().stream()
                .sorted(Comparator.comparingInt(QuestDefinition::order).thenComparing(QuestDefinition::id))).toList();
        Set<String> required = new HashSet<>();
        var byId = new HashMap<String, QuestDefinition>();
        quests.forEach(q -> byId.put(q.id(), q));
        if (guide.id().equals(COURSE) && byId.containsKey(COURSE_GOAL)) {
            var pending = new ArrayDeque<String>();
            pending.add(COURSE_GOAL);
            if (byId.containsKey("7A2C84E05D916B37")) pending.add("7A2C84E05D916B37");
            if (byId.containsKey("6A2C84E05D916B37")) pending.add("6A2C84E05D916B37");
            if (byId.containsKey("2CE36F9B2840D571")) pending.add("2CE36F9B2840D571");
            if (byId.containsKey("1249E56A3FD70C81")) pending.add("1249E56A3FD70C81");
            if (byId.containsKey("1249E5AFB61C7D28")) pending.add("1249E5AFB61C7D28");
            if (byId.containsKey("5D65A27B83E94AF6")) pending.add("5D65A27B83E94AF6");
            if (byId.containsKey("07105D263E94F5A2")) pending.add("07105D263E94F5A2"); // Shared gateway, not optional barter.
            if (byId.containsKey("676BFD158DEB4F7F")) pending.add("676BFD158DEB4F7F");
            if (byId.containsKey("734140DAA3E544D2")) pending.add("734140DAA3E544D2");
            if (byId.containsKey("5620D39F7A4E18B5")) pending.add("5620D39F7A4E18B5");
            if (byId.containsKey("74DB8F5B15F9C370")) pending.add("74DB8F5B15F9C370"); // Fire Resistance and Bastions remain optional.
            if (byId.containsKey(IRON_GOAL)) pending.add(IRON_GOAL);
            if (byId.containsKey("26EC824FB71D3590")) pending.add("26EC824FB71D3590"); // Parallel renewable-food route.
            if (byId.containsKey("42F68D51B39E074C")) pending.add("42F68D51B39E074C"); // Animal care, independent of composting.
            if (byId.containsKey("6D91380C6EA4B2F5")) pending.add("6D91380C6EA4B2F5"); // Storage closes the renewable-supplies route.
            while (!pending.isEmpty()) {
                String id = pending.removeFirst();
                if (required.add(id) && byId.containsKey(id)) {
                    var quest = byId.get(id);
                    if (quest.prerequisiteMode() == QuestDefinition.PrerequisiteMode.ANY) {
                        quest.prerequisiteQuestIds().stream().filter(progress.state().completedQuestIds()::contains)
                                .findFirst().or(() -> quest.prerequisiteQuestIds().stream().findFirst()).ifPresent(pending::add);
                    } else pending.addAll(quest.prerequisiteQuestIds());
                }
            }
        } else required.addAll(byId.keySet());
        var completed = progress.state().completedQuestIds();
        var ready = quests.stream().filter(q -> !completed.contains(q.id())
                && q.prerequisitesMet(completed)).toList();
        QuestDefinition chosen = ready.stream().filter(q -> required.contains(q.id())).findFirst()
                .orElse(ready.isEmpty() ? null : ready.getFirst());
        if (chosen == null) chosen = quests.stream().filter(q -> completed.contains(q.id()) && !q.rewards().isEmpty()
                && !progress.claimedQuestIds().contains(q.id()) && !progress.pendingQuestIds().contains(q.id()))
                .findFirst().orElse(quests.isEmpty() ? null : quests.getFirst());
        if (chosen == null) return GuideBrowserViewModel.Selection.EMPTY;
        String questId = chosen.id();
        var chapter = chapters.stream().filter(c -> c.quests().stream().anyMatch(q -> q.id().equals(questId))).findFirst().orElseThrow();
        return new GuideBrowserViewModel.Selection(guide.id(), chapter.id(), questId);
    }
}
