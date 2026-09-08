package ch.minenox.firsttorch.guide.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/** Bounded, non-consuming inventory projection usable without a Minecraft registry bootstrap. */
final class InventoryCounts {
    private static final int MAX_COUNT = 4096;

    private InventoryCounts() {}

    static <T> Map<String, Integer> collect(Iterable<T> stacks, Function<T, String> itemId,
            ToIntFunction<T> count, Set<String> tagIds, BiPredicate<T, String> matchesTag) {
        Map<String, Integer> counts = new HashMap<>();
        for (T stack : stacks) {
            int amount = Math.clamp(count.applyAsInt(stack), 0, MAX_COUNT);
            if (amount == 0) continue;
            merge(counts, itemId.apply(stack), amount);
            for (String tagId : tagIds) {
                if (matchesTag.test(stack, tagId)) merge(counts, "#" + tagId, amount);
            }
        }
        return counts;
    }

    private static void merge(Map<String, Integer> counts, String key, int amount) {
        counts.merge(key, amount, (left, right) -> Math.min(MAX_COUNT, left + right));
    }
}
