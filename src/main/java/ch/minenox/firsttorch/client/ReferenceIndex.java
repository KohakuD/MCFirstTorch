package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import java.util.List;

/** Indexes only the caller's already-visible chapters, never bypassing visibility gates. */
final class ReferenceIndex {
    private ReferenceIndex() {}

    static List<ChapterDefinition> chapters(List<ChapterDefinition> visible) {
        return visible.stream().filter(chapter -> chapter.titleKey().startsWith("chapter.firsttorch.field_")
                || chapter.titleKey().startsWith("chapter.firsttorch.mechanics_")).toList();
    }
}
