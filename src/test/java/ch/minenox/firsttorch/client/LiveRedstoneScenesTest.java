package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class LiveRedstoneScenesTest {
    private static final String PREFIX = "firsttorch:textures/questpics/";

    @Test void catalogReplacesEveryRemainingRedstoneRaster() {
        Map<String, LiveScene> scenes = LiveRedstoneScenes.scenes();
        assertEquals(13, scenes.size());
        for (String name : List.of("redstone_short_line", "redstone_dust_limit", "redstone_inputs",
                "repeater_direction", "repeater_range", "repeater_delay", "comparator_read",
                "comparator_states", "piston_push", "piston_return", "observer_orientation",
                "observer_pulse", "iron_door_plan")) {
            LiveScene scene = scenes.get(PREFIX + name + ".png");
            assertNotNull(scene, name);
            assertEquals(1672, scene.width());
            assertEquals(941, scene.height());
            assertTrue(scene.wide());
        }
    }

    @Test void plansRetainDirectionalVanillaTopModels() {
        var scenes = LiveRedstoneScenes.scenes();
        assertBlockTop(scenes.get(PREFIX + "repeater_direction.png"), "minecraft:block/repeater_1tick", 1);
        assertBlockTop(scenes.get(PREFIX + "repeater_range.png"), "minecraft:block/repeater_1tick", 1);
        assertBlockTop(scenes.get(PREFIX + "repeater_delay.png"), "minecraft:block/repeater_4tick", 1);
        assertBlockTop(scenes.get(PREFIX + "comparator_read.png"), "minecraft:block/comparator", 1);
        assertBlockTop(scenes.get(PREFIX + "piston_push.png"), "minecraft:block/piston", 1);
        assertBlockTop(scenes.get(PREFIX + "piston_return.png"), "minecraft:block/sticky_piston", 1);
        assertBlockTop(scenes.get(PREFIX + "observer_orientation.png"), "minecraft:block/observer", 3);
    }

    @Test void signalStateAndExplanatoryLabelsAreExplicit() {
        var shortLine = LiveRedstoneScenes.scenes().get(PREFIX + "redstone_short_line.png");
        assertEquals(1, textures(shortLine, "minecraft:textures/block/redstone_lamp_on.png"));
        assertEquals(1, textures(shortLine, "minecraft:textures/block/redstone_lamp.png"));
        assertEquals(List.of(0, 0, 0, 15, 14, 13), wirePowers(shortLine));
        var limit = LiveRedstoneScenes.scenes().get(PREFIX + "redstone_dust_limit.png");
        assertTrue(texts(limit).containsAll(List.of("1–15", "×15", "16", "17")));
        assertEquals(List.of(15, 0, 15), wirePowers(limit));
        var delay = LiveRedstoneScenes.scenes().get(PREFIX + "repeater_delay.png");
        assertEquals(2, blockTops(delay, "minecraft:block/repeater_1tick"));
        assertEquals(2, blockTops(delay, "minecraft:block/repeater_4tick"));
        var pulse = LiveRedstoneScenes.scenes().get(PREFIX + "observer_pulse.png");
        assertEquals(1, textures(pulse, "minecraft:textures/block/redstone_lamp_on.png"));
        assertEquals(2, textures(pulse, "minecraft:textures/block/redstone_lamp.png"));
    }

    @Test void observerAndPistonFaceLegendsUseOriginalTextures() {
        var observer = LiveRedstoneScenes.scenes().get(PREFIX + "observer_orientation.png");
        assertTrue(texts(observer).containsAll(List.of("A", "B", "0", "1", "2")));
        assertEquals(2, observer.operations().stream().filter(LiveScene.Texture.class::isInstance).map(LiveScene.Texture.class::cast)
                .filter(texture -> texture.path().contains("observer_")).count());
        assertTrue(observer.operations().stream().filter(LiveScene.Texture.class::isInstance).map(LiveScene.Texture.class::cast)
                .anyMatch(texture -> texture.path().equals("minecraft:textures/block/observer_front.png")));
        var piston = LiveRedstoneScenes.scenes().get(PREFIX + "piston_return.png");
        assertEquals(1, blockTops(piston, "minecraft:block/piston"));
        assertEquals(1, blockTops(piston, "minecraft:block/sticky_piston"));
        assertEquals(2, piston.operations().stream().filter(LiveScene.Texture.class::isInstance).map(LiveScene.Texture.class::cast)
                .filter(texture -> texture.path().contains("piston_top")).count());
    }

    private static void assertBlockTop(LiveScene scene, String model, int turns) {
        assertTrue(scene.operations().stream().filter(LiveScene.BlockTop.class::isInstance).map(LiveScene.BlockTop.class::cast)
                .anyMatch(block -> block.model().equals(model) && block.quarterTurns() == turns), model);
    }

    private static long blockTops(LiveScene scene, String model) {
        return scene.operations().stream().filter(LiveScene.BlockTop.class::isInstance).map(LiveScene.BlockTop.class::cast)
                .filter(block -> block.model().equals(model)).count();
    }

    private static long textures(LiveScene scene, String path) {
        return scene.operations().stream().filter(LiveScene.Texture.class::isInstance).map(LiveScene.Texture.class::cast)
                .filter(texture -> texture.path().equals(path)).count();
    }

    private static List<String> texts(LiveScene scene) {
        return scene.operations().stream().filter(LiveScene.Text.class::isInstance).map(LiveScene.Text.class::cast)
                .map(LiveScene.Text::value).toList();
    }

    private static List<Integer> wirePowers(LiveScene scene) {
        return scene.operations().stream().filter(LiveScene.Wire.class::isInstance).map(LiveScene.Wire.class::cast)
                .map(LiveScene.Wire::power).toList();
    }
}
