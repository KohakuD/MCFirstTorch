package ch.minenox.firsttorch.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/** Bundled, optional cross-references for quest detail screens. */
public final class QuestReferenceLinks {
    private static final Map<String, List<Link>> LINKS = load();

    private QuestReferenceLinks() {}

    public static List<Link> forQuest(String sourceQuestId) {
        return LINKS.getOrDefault(sourceQuestId, List.of());
    }

    private static Map<String, List<Link>> load() {
        try (var input = QuestReferenceLinks.class.getResourceAsStream("/assets/firsttorch/quest_links.json")) {
            if (input == null) {
                throw new IllegalStateException("Missing bundled quest reference links");
            }
            var type = new TypeToken<Map<String, List<Link>>>() {}.getType();
            Map<String, List<Link>> parsed = new Gson().fromJson(new InputStreamReader(input, StandardCharsets.UTF_8), type);
            return parsed.entrySet().stream().collect(java.util.stream.Collectors.toUnmodifiableMap(
                    Map.Entry::getKey, entry -> List.copyOf(entry.getValue())));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not load bundled quest reference links", exception);
        }
    }

    public record Link(String questId, String titleKey) {}
}
