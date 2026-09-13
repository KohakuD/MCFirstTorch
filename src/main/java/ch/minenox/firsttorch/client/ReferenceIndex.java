package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import java.util.List;
import java.util.Comparator;
import java.util.Locale;
import java.text.Collator;
import java.util.function.Function;

/** Indexes only the caller's already-visible chapters, never bypassing visibility gates. */
final class ReferenceIndex {
    private ReferenceIndex() {}

    static List<ChapterDefinition> chapters(List<ChapterDefinition> visible) {
        return visible.stream().filter(ReferenceIndex::isReference).toList();
    }

    static List<ChapterDefinition> alphabeticalChapters(List<ChapterDefinition> visible,
            Function<String, String> translate, String languageCode) {
        Collator collator = Collator.getInstance(Locale.forLanguageTag(languageCode.replace('_', '-')));
        collator.setStrength(Collator.PRIMARY);
        Comparator<ChapterDefinition> byTitle = (left, right) -> collator.compare(
                translate.apply(left.titleKey()), translate.apply(right.titleKey()));
        return chapters(visible).stream().sorted(byTitle.thenComparing(ChapterDefinition::id)).toList();
    }

    static List<ChapterDefinition> courseChapters(List<ChapterDefinition> visible) {
        return visible.stream().filter(chapter -> !isReference(chapter)).toList();
    }

    private static boolean isReference(ChapterDefinition chapter) {
        return chapter.titleKey().startsWith("chapter.firsttorch.field_")
                || chapter.titleKey().startsWith("chapter.firsttorch.mechanics_");
    }
}
