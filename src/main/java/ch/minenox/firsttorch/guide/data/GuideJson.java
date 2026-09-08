package ch.minenox.firsttorch.guide.data;

import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition.PrerequisiteMode;
import ch.minenox.firsttorch.guide.validation.GuideValidationException;
import ch.minenox.firsttorch.guide.validation.GuideValidator;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public final class GuideJson {
    private static final Gson GSON = new Gson();

    private GuideJson() {
    }

    public static GuideDefinition read(InputStream input) {
        if (input == null) {
            throw new GuideValidationException("Guide input stream must not be null.");
        }
        return read(new InputStreamReader(input, StandardCharsets.UTF_8));
    }

    public static GuideDefinition read(Reader reader) {
        if (reader == null) {
            throw new GuideValidationException("Guide reader must not be null.");
        }
        try {
            JsonElement root = JsonParser.parseReader(reader);
            validatePrerequisiteModes(root);
            GuideDefinition guide = GSON.fromJson(root, GuideDefinition.class);
            GuideValidator.validate(guide);
            return guide;
        } catch (JsonParseException exception) {
            throw new GuideValidationException("Invalid guide JSON: " + exception.getMessage());
        }
    }

    private static void validatePrerequisiteModes(JsonElement root) {
        if (!root.isJsonObject()) return;
        JsonElement chapters = root.getAsJsonObject().get("chapters");
        if (chapters == null || !chapters.isJsonArray()) return;
        for (JsonElement chapter : chapters.getAsJsonArray()) {
            if (!chapter.isJsonObject()) continue;
            JsonElement quests = chapter.getAsJsonObject().get("quests");
            if (quests == null || !quests.isJsonArray()) continue;
            for (JsonElement quest : quests.getAsJsonArray()) {
                if (!quest.isJsonObject()) continue;
                JsonObject object = quest.getAsJsonObject();
                if (!object.has("prerequisiteMode")) continue;
                JsonElement mode = object.get("prerequisiteMode");
                if (!mode.isJsonPrimitive() || !mode.getAsJsonPrimitive().isString()) {
                    throw new GuideValidationException("Quest prerequisiteMode must be ALL or ANY.");
                }
                try {
                    PrerequisiteMode.valueOf(mode.getAsString());
                } catch (IllegalArgumentException exception) {
                    throw new GuideValidationException("Unknown quest prerequisiteMode: " + mode.getAsString());
                }
            }
        }
    }
}
