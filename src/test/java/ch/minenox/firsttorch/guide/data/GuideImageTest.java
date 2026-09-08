package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.GuideImage;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import java.util.List;
import org.junit.jupiter.api.Test;

final class GuideImageTest {
    @Test
    void acceptsValidOptionalImageAndKeepsNullImagesCompatible() {
        assertDoesNotThrow(() -> snapshot(null));
        assertDoesNotThrow(() -> snapshot(new GuideImage(
                "firsttorch:textures/guide/example.png", 640, 360, "guide.test.image.example")));
    }

    @Test
    void rejectsInvalidImageDimensionsPathsAndAltKeys() {
        for (GuideImage image : List.of(
                new GuideImage("firsttorch:textures/guide/example.png", 0, 1, "guide.test.image.example"),
                new GuideImage("firsttorch:textures/../guide/example.png", 1, 1, "guide.test.image.example"),
                new GuideImage("firsttorch:textures/guide/example.png", 1, 1, "invalid"))) {
            assertThrows(IllegalArgumentException.class, () -> snapshot(image));
        }
    }

    private static GuideSnapshot snapshot(GuideImage image) {
        QuestDefinition quest = new QuestDefinition("2000000000000001", 0, "quest.test.title", "quest.test.description",
                new QuestPosition(0, 0), List.of(), List.of(), List.of(), null, image);
        ChapterDefinition chapter = new ChapterDefinition("1000000000000001", 0, "chapter.test.title",
                "chapter.test.description", List.of(quest));
        return new GuideSnapshot(List.of(new GuideDefinition(
                1, "0000000000000001", "guide.test.title", "guide.test.description", List.of(chapter))));
    }
}
