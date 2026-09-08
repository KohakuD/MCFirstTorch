package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

final class RedstoneSignalsTest {
    @BeforeAll static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test void comparatorExerciseMatchesTargetGameContainerSignal() {
        var chest = new SimpleContainer(27);
        assertEquals(0, AbstractContainerMenu.getRedstoneSignalFromContainer(chest));
        chest.setItem(0, cobblestone(1));
        assertEquals(1, AbstractContainerMenu.getRedstoneSignalFromContainer(chest));
        chest.setItem(0, cobblestone(64));
        assertEquals(1, AbstractContainerMenu.getRedstoneSignalFromContainer(chest));
        chest.setItem(1, cobblestone(59));
        assertEquals(1, AbstractContainerMenu.getRedstoneSignalFromContainer(chest));
        chest.setItem(1, cobblestone(60));
        assertEquals(2, AbstractContainerMenu.getRedstoneSignalFromContainer(chest));
        chest.setItem(1, cobblestone(64));
        assertEquals(2, AbstractContainerMenu.getRedstoneSignalFromContainer(chest));
        chest.clearContent();
        assertEquals(0, AbstractContainerMenu.getRedstoneSignalFromContainer(chest));
    }

    @Test void newChaptersKeepLinearOptionalGatesAndSmallRewards() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters();
        assertEquals(List.of("65FC8F24B1D96E0B", "660D9035C2EA7F1C"), chapters.subList(50, 52).stream().map(c -> c.id()).toList());
        assertEquals(List.of(4, 4), chapters.subList(50, 52).stream().map(c -> c.quests().size()).toList());
        String prerequisite = "10A0B0C0D0E00006";
        for (var chapter : chapters.subList(50, 52)) {
            for (var quest : chapter.quests()) {
                assertEquals(List.of(prerequisite), quest.prerequisiteQuestIds());
                prerequisite = quest.id();
            }
            assertEquals(5, chapter.quests().getLast().rewards().getFirst().amount());
            assertTrue(chapter.quests().stream().flatMap(q -> q.rewards().stream()).allMatch(r -> r.itemId() == null));
            assertEquals(1, chapter.quests().stream().flatMap(q -> q.rewards().stream()).count());
        }
        var ids = chapters.subList(50, 52).stream().flatMap(c -> c.quests().stream()).map(q -> q.id()).toList();
        assertTrue(chapters.subList(0, 50).stream().flatMap(c -> c.quests().stream()).flatMap(q -> q.prerequisiteQuestIds().stream()).noneMatch(ids::contains));
    }

    @Test void inventoryCannotProveDirectionDelayOrFillExperiment() throws Exception {
        var snapshot = snapshot();
        var chapters = snapshot.guides().getFirst().chapters().subList(50, 52);
        var tasks = chapters.stream().flatMap(c -> c.quests().stream()).flatMap(q -> q.tasks().stream()).toList();
        assertEquals(11, tasks.size());
        var supplied = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> 128);
        tasks.stream().filter(t -> !t.automatic()).forEach(t -> assertFalse(supplied.completedTaskIds().contains(t.id())));
        for (var task : tasks.stream().filter(t -> t.automatic()).toList()) {
            var low = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals(task.inventoryKey()) ? task.count() - 1 : 0);
            assertFalse(low.completedTaskIds().contains(task.id()));
            var exact = TaskEvaluator.evaluate(snapshot, low, key -> key.equals(task.inventoryKey()) ? task.count() : 0);
            assertTrue(exact.completedTaskIds().contains(task.id()));
        }
    }

    private static ItemStack cobblestone(int count) {
        return new ItemStack(Holder.direct(Items.COBBLESTONE, DataComponentMap.builder()
                .set(DataComponents.MAX_STACK_SIZE, 64).build()), count);
    }

    private static GuideSnapshot snapshot() throws Exception {
        try (var input = RedstoneSignalsTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
