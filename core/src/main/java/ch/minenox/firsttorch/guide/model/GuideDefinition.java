package ch.minenox.firsttorch.guide.model;

import java.util.List;

public record GuideDefinition(
        int schemaVersion,
        String id,
        String titleKey,
        String descriptionKey,
        List<ChapterDefinition> chapters) {

    public GuideDefinition {
        chapters = chapters == null ? null : List.copyOf(chapters);
    }
}
