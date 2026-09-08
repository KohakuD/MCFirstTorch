package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

final class RedstoneMotionTest {
    @BeforeAll static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test void targetObserverOnlySignalsAlongItsOutputAxisWhenPowered() {
        for (var facing : Direction.values()) {
            var state = Blocks.OBSERVER.defaultBlockState().setValue(ObserverBlock.FACING, facing);
            for (var query : Direction.values()) {
                assertEquals(0, state.getSignal(null, BlockPos.ZERO, query));
                // The query direction is from the receiver towards the Observer.
                assertEquals(query == facing ? 15 : 0,
                        state.setValue(ObserverBlock.POWERED, true).getSignal(null, BlockPos.ZERO, query));
            }
        }
        assertEquals(12, PistonStructureResolver.MAX_PUSH_DEPTH);
    }

    @Test void motionChaptersHaveSmallLinearMapsAndNeverGateOlderContent() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters();
        assertEquals(List.of("671EA146D3FB802D", "682FB257E40C913E"), chapters.subList(52, 54).stream().map(c -> c.id()).toList());
        String previous = "12A0B0C0D0E00004";
        for (var chapter : chapters.subList(52, 54)) {
            assertEquals(4, chapter.quests().size());
            for (var quest : chapter.quests()) {
                assertEquals(List.of(previous), quest.prerequisiteQuestIds());
                previous = quest.id();
            }
            assertEquals(1, chapter.quests().stream().flatMap(q -> q.rewards().stream()).count());
            assertEquals(5, chapter.quests().getLast().rewards().getFirst().amount());
            assertNull(chapter.quests().getLast().rewards().getFirst().itemId());
        }
        var ids = chapters.subList(52, 54).stream().flatMap(c -> c.quests().stream()).map(q -> q.id()).toList();
        assertTrue(chapters.subList(0, 52).stream().flatMap(c -> c.quests().stream())
                .flatMap(q -> q.prerequisiteQuestIds().stream()).noneMatch(ids::contains));
    }

    @Test void owningComponentsNeverProvesMovementOrObservation() throws Exception {
        var snapshot = snapshot();
        var tasks = snapshot.guides().getFirst().chapters().subList(52, 54).stream()
                .flatMap(c -> c.quests().stream()).flatMap(q -> q.tasks().stream()).toList();
        assertEquals(8, tasks.size());
        assertEquals(List.of("minecraft:piston", "minecraft:sticky_piston", "minecraft:observer"),
                tasks.stream().filter(t -> t.automatic()).map(t -> t.itemId()).toList());
        var result = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> 1);
        for (var task : tasks) assertEquals(task.automatic(), result.completedTaskIds().contains(task.id()));
        assertTrue(result.completedQuestIds().isEmpty());
    }

    private static GuideSnapshot snapshot() throws Exception {
        try (var input = RedstoneMotionTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
