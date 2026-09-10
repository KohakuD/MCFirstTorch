package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.Test;

final class LiveEndScenesTest {
    private static final String PREFIX = "firsttorch:textures/questpics/";

    @Test void catalogContainsEveryMigratedNonScreenshotEndDiagram() {
        var scenes = LiveEndScenes.scenes();
        var expected = Set.of("end_crystal_exposed", "end_crystal_removal",
                "end_portal_final_eye", "end_portal_frame_states", "end_portal_room",
                "ender_dragon_flight", "ender_dragon_perched", "ender_eye_search", "enderman_roof_shelter",
                "outer_end_arrival", "dragon_egg_retrieval", "chorus_fruit_safety",
                "shulker_levitation", "elytra_water_course");
        assertEquals(expected.size(), scenes.size());
        for (var name : expected) assertTrue(scenes.containsKey(PREFIX + name + ".png"), name);
        assertFalse(scenes.containsKey(PREFIX + "end_portal_room_capture.png"));
        assertFalse(scenes.containsKey(PREFIX + "end_arrival_platform_capture.png"));
        for (var capture : Set.of("end_battlefield", "end_city_search", "end_exit_portal",
                "end_gateway_access", "end_island_crossing", "chorus_harvest")) {
            assertFalse(scenes.containsKey(PREFIX + capture + ".png"), capture);
        }
    }

    @Test void scenesAreNativeGeometryWithValidCanvasesAndOperations() {
        for (var entry : LiveEndScenes.scenes().entrySet()) {
            var scene = entry.getValue();
            assertEquals(600, scene.width(), entry.getKey());
            assertEquals(340, scene.height(), entry.getKey());
            assertFalse(scene.operations().isEmpty(), entry.getKey());
            assertTrue(scene.operations().stream().anyMatch(LiveScene.Op.class::isInstance), entry.getKey());
        }
    }

    @Test void multiPanelLessonsUseWideReadingColumns() {
        var scenes = LiveEndScenes.scenes();
        for (var name : Set.of("end_crystal_removal", "end_portal_frame_states", "ender_eye_search",
                "enderman_roof_shelter", "dragon_egg_retrieval", "shulker_levitation",
                "elytra_water_course")) {
            assertTrue(scenes.get(PREFIX + name + ".png").wide(), name);
        }
    }

    @Test void finalEyePreservesTwelveFramesWithExactlyOneEmptySocket() {
        var frames = scene("end_portal_final_eye").operations().stream()
                .filter(LiveScene.BlockTop.class::isInstance).map(LiveScene.BlockTop.class::cast).toList();
        assertEquals(12, frames.size());
        assertEquals(11, frames.stream().filter(f -> f.model().endsWith("_filled")).count());
        for (int turn=0;turn<4;turn++) {
            int direction=turn;
            assertEquals(3,frames.stream().filter(f -> f.quarterTurns()==direction).count());
        }
    }

    @Test void shelterCutawayPreservesExactlyTwoClearBlocksAndPlayerFits() {
        var ops=scene("enderman_roof_shelter").operations();
        var roof=ops.stream().filter(LiveScene.Texture.class::isInstance).map(LiveScene.Texture.class::cast)
                .filter(t -> t.path().endsWith("/end_stone.png") && t.x()>=335 && t.y()==120).toList();
        assertEquals(3,roof.size());
        for(var tile:roof) assertEquals(2*tile.height(),270-(tile.y()+tile.height()));
        var player=ops.stream().filter(LiveScene.Entity.class::isInstance).map(LiveScene.Entity.class::cast)
                .filter(e->e.id().equals("minecraft:player")).findFirst().orElseThrow();
        assertTrue(player.y()>=170);
        assertEquals(270,player.y()+player.height());
    }

    @Test void chorusSafetyShowsFullSeventeenBySeventeenTestArea() {
        assertEquals(17*17,scene("chorus_fruit_safety").operations().stream()
                .filter(LiveScene.Texture.class::isInstance).map(LiveScene.Texture.class::cast)
                .filter(t->t.path().endsWith("/end_stone.png") && t.width()==16 && t.height()==16).count());
    }

    @Test void cageAndEggSafetyStepsRemainNumbered() {
        for(var lesson:Set.of("end_crystal_removal","dragon_egg_retrieval")) {
            var numbers=scene(lesson).operations().stream().filter(LiveScene.Text.class::isInstance)
                    .map(LiveScene.Text.class::cast).filter(t->!t.translated()).map(LiveScene.Text::value).toList();
            assertTrue(numbers.containsAll(lesson.equals("dragon_egg_retrieval") ? Set.of("1","2","3") : Set.of("1","2")));
        }
    }

    private static LiveScene scene(String name) {
        return LiveEndScenes.scenes().get(PREFIX+name+".png");
    }
}
