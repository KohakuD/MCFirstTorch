package ch.minenox.firsttorch.guide.data;

import ch.minenox.firsttorch.guide.model.QuestDefinition;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class Backport1211CurriculumTest {
    private static Map<String, QuestDefinition> quests() {
        var guide = GuideJson.read(Backport1211CurriculumTest.class.getResourceAsStream("/data/firsttorch/guides/course.json"));
        return guide.chapters().stream().flatMap(c -> c.quests().stream())
                .collect(Collectors.toMap(QuestDefinition::id, q -> q));
    }

    @Test void excursionsDoNotRequireAdvancedLodestoneAndLodestoneKeepsNativeObservation() {
        var quests = quests();
        assertTrue(quests.get("12E8A5C74F309BD6").prerequisiteQuestIds().contains("2F6A91C4D8E307B5"));
        assertTrue(quests.values().stream().noneMatch(q -> q.prerequisiteQuestIds().contains("0BED012A8146C359")));
        var task = quests.get("0BED012A8146C359").tasks().getFirst();
        assertEquals("3E1045DAB479F68C", task.id());
        assertEquals("minecraft:nether/use_lodestone", task.advancementId());
        assertEquals("use_lodestone", task.criterion());
        quests.values().forEach(q -> q.prerequisiteQuestIds().forEach(id -> assertTrue(quests.containsKey(id), id)));
    }

    @Test void unavailableContentIsExcludedAndRewardIdSurvivesAdaptation() {
        var quests = quests();
        assertFalse(quests.containsKey("1CA0B0C0D0E00003"));
        var armour = quests.get("18A6D3F90C754BE2");
        assertEquals("minecraft:iron_chestplate", armour.iconItemId());
        assertEquals(Set.of("3C16B9E50A724DF8"), Set.copyOf(armour.prerequisiteQuestIds()));
        var reward = quests.get("6D91380C6EA4B2F5").rewards().stream()
                .filter(r -> r.id().equals("5AEFB70D418269C3")).findFirst().orElseThrow();
        assertEquals("minecraft:chest", reward.itemId());
        assertTrue(quests.values().stream().noneMatch(q -> "minecraft:bundle".equals(q.iconItemId())));
    }

    @Test void bothLocalesAndReferenceLinksCoverTheTargetCurriculum() throws Exception {
        var en = json("/assets/firsttorch/lang/en_us.json");
        var de = json("/assets/firsttorch/lang/de_de.json");
        assertEquals(en.keySet(), de.keySet());
        var quests = quests();
        quests.values().forEach(q -> {
            assertTrue(en.has(q.titleKey()), q.titleKey());
            assertTrue(en.has(q.descriptionKey()), q.descriptionKey());
            q.tasks().forEach(t -> assertTrue(en.has(t.titleKey()), t.titleKey()));
        });
        json("/assets/firsttorch/quest_links.json").entrySet().forEach(entry ->
                entry.getValue().getAsJsonArray().forEach(link ->
                        assertTrue(quests.containsKey(link.getAsJsonObject().get("questId").getAsString()))));
    }

    private static JsonObject json(String path) throws Exception {
        try (var input = Backport1211CurriculumTest.class.getResourceAsStream(path)) {
            assertNotNull(input, path);
            return JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }
}
