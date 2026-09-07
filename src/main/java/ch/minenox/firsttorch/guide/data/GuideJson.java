package ch.minenox.firsttorch.guide.data;

import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.validation.GuideValidationException;
import ch.minenox.firsttorch.guide.validation.GuideValidator;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
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
            GuideDefinition guide = GSON.fromJson(reader, GuideDefinition.class);
            GuideValidator.validate(guide);
            return guide;
        } catch (JsonParseException exception) {
            throw new GuideValidationException("Invalid guide JSON: " + exception.getMessage());
        }
    }
}
