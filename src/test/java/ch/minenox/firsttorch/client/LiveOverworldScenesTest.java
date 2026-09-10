package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class LiveOverworldScenesTest {
    @Test void allRemainingDiagramResourcesHaveLiveScenesAndNoRasterCopies() {
        assertEquals(20, LiveOverworldScenes.resources().size());
        for (String resource : LiveOverworldScenes.resources()) {
            var scene = LiveOverworldScenes.find(resource);
            assertNotNull(scene);
            assertEquals(600, scene.width());
            assertEquals(340, scene.height());
            assertFalse(scene.operations().isEmpty());
            assertNull(getClass().getResourceAsStream("/assets/" + resource.replace(':', '/')), resource);
        }
        assertNull(LiveOverworldScenes.find("firsttorch:textures/questpics/nether_fortress.png"));
        assertNull(LiveOverworldScenes.find("firsttorch:textures/questpics/stronghold_iron_door_capture.png"));
    }

    @Test void diagramsUseInstalledModelsAndMeaningfulTeachingGeometry() {
        var mobs = LiveOverworldScenes.find("firsttorch:textures/questpics/hostile_mob_overview.png");
        assertEquals(4, mobs.operations().stream().filter(LiveScene.Entity.class::isInstance).count());
        var farm = LiveOverworldScenes.find("firsttorch:textures/questpics/farmland_9x9.png");
        assertEquals(80, farm.operations().stream().filter(LiveScene.BlockTop.class::isInstance).count());
        assertEquals(1, farm.operations().stream().filter(LiveScene.TintedTexture.class::isInstance).count());
        var trade = LiveOverworldScenes.find("firsttorch:textures/questpics/villager_trading.png");
        assertTrue(trade.operations().stream().anyMatch(LiveScene.Item.class::isInstance));
        assertTrue(trade.operations().stream().filter(LiveScene.Texture.class::isInstance)
                .map(LiveScene.Texture.class::cast).anyMatch(t -> t.path().endsWith("container/villager.png")
                        && t.u1() == 276F / 512 && t.v1() == 166F / 256));
        assertTrue(LiveOverworldScenes.find("firsttorch:textures/questpics/boat_controls.png").wide());
    }

    @Test void bookshelfPlanLeavesOneEmptyBlockAroundTableAndOneEntrance() {
        var scene = LiveOverworldScenes.find("firsttorch:textures/questpics/enchanting_bookshelves.png");
        var shelves = scene.operations().stream().filter(LiveScene.Texture.class::isInstance)
                .map(LiveScene.Texture.class::cast).filter(t -> t.path().endsWith("/bookshelf.png")).toList();
        assertEquals(15, shelves.size());
        for (var shelf : shelves) {
            int column = (shelf.x() - 173) / 52, row = (shelf.y() - 29) / 52;
            assertTrue(column == 0 || column == 4 || row == 0 || row == 4);
            assertFalse(column == 2 && row == 4);
        }
    }

    @Test void portalUsesTenObsidianAndSixOriginalPortalFaces() {
        for (String state : new String[]{"frame", "lit"}) {
            var textures = LiveOverworldScenes.find("firsttorch:textures/questpics/nether_portal_" + state + ".png")
                    .operations().stream().filter(LiveScene.Texture.class::isInstance).map(LiveScene.Texture.class::cast).toList();
            assertEquals(10, textures.stream().filter(t -> t.path().endsWith("/obsidian.png")).count());
            assertEquals(state.equals("lit") ? 6 : 0, textures.stream().filter(t -> t.path().endsWith("/nether_portal.png")).count());
        }
    }

    @Test void entityComparisonsPreserveTheOriginalTypesAndBabyState() {
        var hazards = LiveOverworldScenes.find("firsttorch:textures/questpics/fortress_hazards.png").operations().stream()
                .filter(LiveScene.Entity.class::isInstance).map(LiveScene.Entity.class::cast).map(LiveScene.Entity::id).toList();
        assertEquals(java.util.List.of("minecraft:blaze", "minecraft:wither_skeleton", "minecraft:magma_cube"), hazards);
        var piglins = LiveOverworldScenes.find("firsttorch:textures/questpics/piglin_comparison.png").operations().stream()
                .filter(LiveScene.Entity.class::isInstance).map(LiveScene.Entity.class::cast).map(LiveScene.Entity::id).toList();
        assertEquals(java.util.List.of("minecraft:piglin", "minecraft:piglin_brute", "minecraft:piglin#baby", "minecraft:zombified_piglin"), piglins);
    }

    @Test void waterSourcesShowBothInitialBucketPlacementsAndCentreCollection() {
        var items = LiveOverworldScenes.find("firsttorch:textures/questpics/infinite_water_sources.png").operations().stream()
                .filter(LiveScene.Item.class::isInstance).map(LiveScene.Item.class::cast).toList();
        assertEquals(4, items.stream().filter(i -> i.id().equals("minecraft:water_bucket")).count());
        assertEquals(1, items.stream().filter(i -> i.id().equals("minecraft:bucket")).count());
    }
}
