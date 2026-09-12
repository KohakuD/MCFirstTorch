package ch.minenox.firsttorch.guide.edition;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Read-only comparison: never changes guide definitions, completion or reward claims. */
public final class EditionHistory {
    public enum Kind { NEW, REVISED, CONTEXT }
    public record Change(String questId, String edition, Kind kind) {
        public Change {
            if (questId == null || !questId.matches("[0-7][0-9A-F]{15}"))
                throw new IllegalArgumentException("Invalid quest ID");
            if (edition == null || kind == null) throw new IllegalArgumentException("Missing change fields");
        }
    }
    public record Comparison(Set<String> changedQuestIds, Set<String> contextQuestIds) {
        public Comparison {
            changedQuestIds = Collections.unmodifiableSet(new LinkedHashSet<>(changedQuestIds));
            contextQuestIds = Collections.unmodifiableSet(new LinkedHashSet<>(contextQuestIds));
        }
    }

    private final List<String> editions;
    private final List<Change> changes;

    /** Editions must be authored in Minecraft order, independent of mod release dates. */
    public EditionHistory(List<String> editions, List<Change> changes) {
        this.editions = List.copyOf(editions);
        this.changes = List.copyOf(changes);
        if (editions.isEmpty() || new HashSet<>(editions).size() != editions.size()
                || editions.stream().anyMatch(String::isBlank))
            throw new IllegalArgumentException("Invalid edition order");
        Set<String> pairs = new HashSet<>();
        for (var change : changes) {
            index(change.edition());
            if (!pairs.add(change.edition() + "/" + change.questId()))
                throw new IllegalArgumentException("Duplicate lesson change in edition");
        }
    }

    /** Publication eligibility is supplied by the runtime catalogue, never inferred from a version string. */
    public List<String> baselines(String current, Set<String> publishedEditions) {
        return editions.subList(0, index(current)).stream().filter(publishedEditions::contains).toList();
    }

    public Comparison compare(String baseline, String current, GuideSnapshot snapshot) {
        int from = index(baseline), to = index(current);
        if (from >= to) throw new IllegalArgumentException("Baseline must precede the current edition");
        Map<String, QuestDefinition> available = new LinkedHashMap<>();
        snapshot.guides().forEach(g -> g.chapters().forEach(c -> c.quests().forEach(q -> available.put(q.id(), q))));
        Set<String> selected = new LinkedHashSet<>(), context = new LinkedHashSet<>();
        for (var change : changes) {
            int at = index(change.edition());
            if (at > from && at <= to && available.containsKey(change.questId())) {
                (change.kind() == Kind.CONTEXT ? context : selected).add(change.questId());
            }
        }
        // Include all alternative prerequisites as navigable context, not as automatic completion.
        // The server retains ALL/ANY eligibility checks and original reward protection.
        var pending = new ArrayDeque<String>();
        pending.addAll(selected);
        pending.addAll(context);
        Set<String> visited = new HashSet<>();
        while (!pending.isEmpty()) {
            var id = pending.removeFirst();
            if (!visited.add(id)) continue;
            for (var prerequisite : available.get(id).prerequisiteQuestIds()) {
                if (available.containsKey(prerequisite)) {
                    context.add(prerequisite);
                    pending.addLast(prerequisite);
                }
            }
        }
        context.removeAll(selected);
        return new Comparison(selected, context);
    }

    private int index(String edition) {
        int index = editions.indexOf(edition);
        if (index < 0) throw new IllegalArgumentException("Unknown Minecraft edition: " + edition);
        return index;
    }
}
