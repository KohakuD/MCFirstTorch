package ch.minenox.firsttorch.guide.data;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.validation.GuideValidationException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class GuideResourceLoader {
    private GuideResourceLoader() {
    }

    public static GuideSnapshot load(Map<String, ReaderSource> resources) {
        List<Map.Entry<String, ReaderSource>> orderedResources = resources.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .toList();
        List<GuideDefinition> guides = new ArrayList<>(orderedResources.size());

        for (Map.Entry<String, ReaderSource> entry : orderedResources) {
            try (Reader reader = entry.getValue().open()) {
                guides.add(GuideJson.read(reader));
            } catch (GuideValidationException | IOException exception) {
                throw new GuideValidationException(
                        "Failed to load guide resource '" + entry.getKey() + "': " + exception.getMessage());
            }
        }

        try {
            return new GuideSnapshot(guides);
        } catch (GuideValidationException exception) {
            throw new GuideValidationException("Loaded guide set is invalid: " + exception.getMessage());
        }
    }

    @FunctionalInterface
    public interface ReaderSource {
        Reader open() throws IOException;
    }
}
