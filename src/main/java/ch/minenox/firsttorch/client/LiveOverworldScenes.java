package ch.minenox.firsttorch.client;

import java.util.Map;

/** Teaching layouts composed from installed vanilla models, block faces and interface sprites. */
final class LiveOverworldScenes {
    private static final int DARK = 0xFF202124, PANEL = 0xFF34363A, ORANGE = 0xFFFF9800;
    private static final int WHITE = 0xFFF2F2F2, RED = 0xFFE53935, BLUE = 0xFF42A5F5, GREEN = 0xFF66BB6A;
    private static final String BLOCK = "minecraft:textures/block/", HUD = "minecraft:textures/gui/sprites/hud/";
    private static final Map<String, LiveScene> SCENES = Map.ofEntries(
            entry("attack_and_break", attackAndBreak()), entry("attack_indicator", attackIndicator()),
            entry("boat_controls", boatControls()), entry("enchanting_bookshelves", bookshelves()),
            entry("farmland_9x9", farmland()), entry("first_night_shelter", shelter()),
            entry("fortress_hazards", fortressHazards()), entry("hostile_mob_overview", hostileMobs()),
            entry("hunger_and_eating", hunger()), entry("infinite_water_sources", waterSources()),
            entry("nether_portal_frame", portal(false)), entry("nether_portal_lit", portal(true)),
            entry("nether_route_marker", routeMarker()), entry("piglin_comparison", piglins()),
            entry("place_crafting_table", craftingTable()), entry("safe_staircase", staircase()),
            entry("stronghold_iron_door", ironDoor()), entry("stronghold_silverfish", silverfish()),
            entry("torch_route", torchRoute()), entry("villager_trading", trading()));

    private LiveOverworldScenes() {}
    static LiveScene find(String resource) { return SCENES.get(resource); }
    static Map<String, LiveScene> scenes() { return SCENES; }
    static java.util.Set<String> resources() { return SCENES.keySet(); }
    private static Map.Entry<String, LiveScene> entry(String name, LiveScene scene) {
        return Map.entry("firsttorch:textures/questpics/" + name + ".png", scene);
    }
    private static LiveScene.Builder scene(boolean wide) { return new LiveScene.Builder(600, 340, wide).fill(0, 0, 600, 340, DARK); }
    private static void tile(LiveScene.Builder b, String name, int x, int y, int size) {
        b.texture(BLOCK + name + ".png", x, y, size, size);
    }
    private static void water(LiveScene.Builder b, int x, int y, int size) {
        b.tintedCrop(BLOCK + "water_still.png", x, y, size, size, 0, 0, 1, 1F / 32, 0xFF3F76E4);
    }
    private static void cross(LiveScene.Builder b, int x, int y, int size) {
        b.line(x, y, x + size, y + size, 5, RED).line(x + size, y, x, y + size, 5, RED);
    }
    private static void label(LiveScene.Builder b, String key, int x, int y, int width) {
        b.key("image.firsttorch.live." + key, x, y, width, 20);
    }

    private static LiveScene attackAndBreak() {
        var b = scene(true);
        b.item("minecraft:oak_log", 45, 65, 90).arrow(145, 118, 220, 118, 4, ORANGE);
        tile(b, "oak_log", 240, 58, 120);
        b.texture(BLOCK + "destroy_stage_6.png", 240, 58, 120, 120)
                .texture(HUD + "crosshair.png", 286, 105, 28, 28)
                .arrow(375, 118, 440, 118, 4, ORANGE).item("minecraft:oak_log", 470, 88, 60);
        label(b, "hold_attack", 115, 225, 370);
        return b.build();
    }
    private static LiveScene attackIndicator() {
        var b = scene(true);
        b.texture(HUD + "crosshair.png", 116, 62, 80, 80)
                .texture(HUD + "crosshair_attack_indicator_background.png", 116, 158, 80, 20)
                .crop(HUD + "crosshair_attack_indicator_progress.png", 116, 158, 35, 20, 0, 0, .4375F, 1)
                .texture(HUD + "crosshair.png", 404, 62, 80, 80)
                .texture(HUD + "crosshair_attack_indicator_full.png", 404, 158, 80, 80)
                .arrow(250, 150, 350, 150, 4, ORANGE);
        label(b, "wait", 45, 266, 225); label(b, "ready", 330, 266, 225);
        return b.build();
    }
    private static LiveScene boatControls() {
        var b = scene(true);
        for (int panel = 0; panel < 3; panel++) {
            int x = panel * 200 + 10;
            for (int row = 0; row < 5; row++) for (int col = 0; col < 3; col++) water(b, x + col * 60, 35 + row * 45, 60);
            b.outline(x, 35, 180, 270, WHITE).text("" + (panel + 1), x + 8, 40, 25, 20);
        }
        b.item("minecraft:oak_boat", 65, 95, 75);
        label(b, "place_board", 18, 245, 164);
        b.arrow(100, 227, 100, 174, 4, ORANGE).item("minecraft:oak_boat", 258, 78, 78)
                .key("key.keyboard.w", 282, 180, 30, 22).key("key.keyboard.a", 248, 213, 30, 22)
                .key("key.keyboard.s", 282, 213, 30, 22).key("key.keyboard.d", 316, 213, 30, 22)
                .arrow(280, 150, 240, 130, 3, ORANGE).arrow(318, 150, 357, 130, 3, ORANGE);
        for (int row = 0; row < 5; row++) tile(b, "sand", 530, 35 + row * 45, 60);
        b.item("minecraft:oak_boat", 437, 103, 70).entity("minecraft:player", 530, 87, 48, 105)
                .arrow(495, 180, 539, 180, 3, ORANGE).key("key.keyboard.left.shift", 417, 205, 170, 20);
        label(b, "break_collect", 418, 254, 166);
        return b.build();
    }
    private static LiveScene bookshelves() {
        var b = scene(false);
        // The perimeter of a 5x5 square holds 16 blocks; leave one entrance = 15 shelves.
        for (int row = 0; row < 5; row++) for (int col = 0; col < 5; col++) {
            int x = 170 + col * 52, y = 26 + row * 52;
            b.outline(x, y, 50, 50, PANEL);
            if ((row == 0 || row == 4 || col == 0 || col == 4) && !(row == 4 && col == 2))
                tile(b, "bookshelf", x + 3, y + 3, 44);
        }
        b.blockTop("minecraft:enchanting_table", 0, 277, 133, 44).outline(221, 77, 158, 158, ORANGE)
                .text("15", 80, 95, 55, 25).item("minecraft:bookshelf", 80, 136, 55);
        label(b, "empty_gap", 165, 301, 270);
        return b.build();
    }
    private static LiveScene farmland() {
        var b = scene(false);
        for (int row = 0; row < 9; row++) for (int col = 0; col < 9; col++) {
            int x = 174 + col * 28, y = 43 + row * 28;
            if (col == 4 && row == 4) water(b, x, y, 27);
            else b.blockTop("minecraft:farmland_moist", 0, x, y, 27);
        }
        b.arrow(300, 22, 300, 145, 3, BLUE).text("9 × 9", 250, 308, 100, 20)
                .text("4", 335, 162, 25, 20).arrow(422, 175, 320, 175, 3, BLUE);
        return b.build();
    }
    private static LiveScene shelter() {
        var b = scene(false);
        int cell = 48, left = 154, top = 38;
        for (int row = 0; row < 5; row++) for (int col = 0; col < 7; col++)
            if (row < 2 || row == 4 || col == 0 || col >= 5)
                tile(b, row == 0 ? "grass_block_side" : "dirt", left + col * cell, top + row * cell, cell);
        b.outline(left + cell, top + 2 * cell, 4 * cell, 2 * cell, WHITE);
        for (int row = 2; row <= 3; row++) b.outline(left + 3, top + row * cell + 3, cell - 6, cell - 6, ORANGE);
        b.arrow(76, 182, 144, 182, 5, ORANGE).text("2", 210, 171, 25, 20)
                .arrow(248, 146, 248, 220, 3, WHITE);
        label(b, "seal_entrance", 150, 301, 345);
        return b.build();
    }
    private static LiveScene fortressHazards() {
        return scene(true).outline(10, 30, 180, 280, WHITE).outline(210, 30, 180, 280, WHITE).outline(410, 30, 180, 280, WHITE)
                .entity("minecraft:blaze", 48, 70, 100, 180).entity("minecraft:wither_skeleton", 252, 48, 95, 225)
                .entity("minecraft:magma_cube", 440, 130, 120, 130).build();
    }
    private static LiveScene hostileMobs() {
        return scene(true).entity("minecraft:zombie", 35, 45, 85, 170).item("minecraft:rotten_flesh", 55, 250, 42)
                .entity("minecraft:skeleton", 182, 45, 85, 170).item("minecraft:bone", 204, 250, 42)
                .entity("minecraft:spider", 319, 93, 108, 92).item("minecraft:string", 353, 250, 42)
                .entity("minecraft:creeper", 482, 45, 85, 170).item("minecraft:gunpowder", 502, 250, 42).build();
    }
    private static LiveScene hunger() {
        var b = scene(true);
        for (int panel = 0; panel < 2; panel++) {
            int x = panel * 300 + 15;
            b.outline(x, 20, 270, 300, WHITE).text("" + (panel + 1), x + 10, 30, 30, 25);
            for (int n = 0; n < 10; n++) b.texture(HUD + (panel == 0 && n >= 3 ? "food_empty.png" : "food_full.png"), x + 12 + n * 24, 180, 24, 24);
            b.texture(HUD + "hotbar.png", x + 11, 250, 248, 30);
        }
        b.arrow(260, 135, 340, 135, 4, ORANGE).item("minecraft:bread", 412, 72, 52)
                .item("minecraft:bread", 330, 253, 24).outline(327, 249, 29, 31, ORANGE);
        label(b, "hold_use", 340, 130, 225);
        return b.build();
    }
    private static LiveScene waterSources() {
        var b = scene(true).line(300, 20, 300, 320, 2, PANEL);
        for (int row = 0; row < 2; row++) for (int col = 0; col < 2; col++) {
            int x = 68 + col * 72, y = 105 + row * 72; water(b, x, y, 71);
            b.outline(x, y, 71, 71, WHITE);
            if (row == col) b.outline(x + 4, y + 4, 63, 63, ORANGE);
        }
        b.item("minecraft:water_bucket", 83, 27, 42).arrow(104, 74, 104, 99, 3, ORANGE)
                .item("minecraft:water_bucket", 155, 286, 42).arrow(176, 280, 176, 254, 3, ORANGE);
        for (int col = 0; col < 3; col++) {
            int x = 330 + col * 78; water(b, x, 147, 77); b.outline(x + 3, 150, 71, 71, col == 1 ? WHITE : ORANGE);
            if (col != 1) b.item("minecraft:water_bucket", x + 17, 46, 44).arrow(x + 39, 95, x + 39, 139, 3, ORANGE);
        }
        b.item("minecraft:bucket", 425, 279, 44).arrow(447, 270, 447, 232, 3, WHITE);
        return b.build();
    }
    private static void portalFrame(LiveScene.Builder b, int x, int y, int cell, boolean lit) {
        // Minimum frame: two horizontal pairs plus two vertical triples, no corners.
        for (int row = 0; row < 5; row++) for (int col = 0; col < 4; col++) {
            boolean border = (row == 0 || row == 4) ? col == 1 || col == 2 : col == 0 || col == 3;
            if (border) tile(b, "obsidian", x + col * cell, y + row * cell, cell);
            else if (lit && row > 0 && row < 4 && col > 0 && col < 3)
                b.crop(BLOCK + "nether_portal.png", x + col * cell, y + row * cell, cell, cell, 0, 0, 1, 1F / 32);
        }
    }
    private static LiveScene portal(boolean lit) {
        var b = scene(false); portalFrame(b, 204, 39, 48, lit);
        b.text("4", 279, 12, 40, 20).text("5", 165, 146, 30, 20);
        if (!lit) b.item("minecraft:obsidian", 431, 52, 48).text("10", 435, 107, 42, 23)
                .item("minecraft:flint_and_steel", 438, 195, 50).arrow(432, 243, 311, 224, 4, ORANGE);
        return b.build();
    }
    private static LiveScene routeMarker() {
        var b = scene(false); portalFrame(b, 53, 92, 30, true);
        // Front elevation makes the two stacked blocks and portal-facing torch explicit.
        tile(b, "cobblestone", 408, 73, 90); tile(b, "cobblestone", 408, 163, 90);
        b.item("minecraft:torch", 373, 105, 56).outline(369, 103, 58, 60, ORANGE)
                .arrow(359, 177, 194, 177, 5, ORANGE).text("2", 518, 155, 35, 24);
        return b.build();
    }
    private static LiveScene piglins() {
        var b = scene(true);
        String[] ids = {"piglin", "piglin_brute", "piglin#baby", "zombified_piglin"};
        for (int i = 0; i < ids.length; i++) {
            int x = 8 + i * 150; b.outline(x, 25, 134, 285, i == 0 ? GREEN : WHITE);
            b.entity("minecraft:" + ids[i], x + 12, i == 2 ? 121 : 54, 110, i == 2 ? 128 : 205);
            if (i > 0) cross(b, x + 25, 264, 30);
        }
        b.item("minecraft:gold_ingot", 57, 267, 31).item("minecraft:golden_axe", 255, 202, 31);
        return b.build();
    }
    private static LiveScene craftingTable() {
        var b = scene(true).line(300, 15, 300, 325, 2, PANEL);
        b.text("1", 15, 20, 30, 24)
                .crop("minecraft:textures/gui/container/inventory.png", 20, 72, 259, 122, 7F / 256, 83F / 256, 169F / 256, 159F / 256)
                .item("minecraft:crafting_table", 234, 78, 23)
                .texture(HUD + "hotbar.png", 15, 235, 270, 33)
                .item("minecraft:crafting_table", 138, 239, 26).outline(135, 234, 33, 35, ORANGE)
                .arrow(245, 108, 152, 219, 4, ORANGE).text("2", 315, 20, 30, 24);
        for (int col = 0; col < 3; col++) b.item("minecraft:grass_block", 330 + col * 71, 161, 74);
        b.item("minecraft:crafting_table", 401, 109, 74);
        label(b, "place_ground", 320, 274, 266);
        return b.build();
    }
    private static LiveScene staircase() {
        var b = scene(true);
        // Three air blocks per step leave headroom while jumping onto the next full block.
        for (int panel = 0; panel < 2; panel++) for (int row = 0; row < 9; row++) for (int col = 0; col < 8; col++) {
            boolean air = panel == 0 ? col == 3 && row < 5 || col >= 3 && col <= 6 && row >= 5 && row <= 7
                    : col >= 1 && col <= 6 && row >= col - 2 && row <= col;
            if (!air) tile(b, row == 0 ? "dirt" : "stone", 18 + panel * 300 + col * 33, 24 + row * 30, 33);
        }
        tile(b, "stone", 117, 144, 33);
        b.texture(BLOCK + "destroy_stage_7.png", 117, 144, 33, 33)
                .entity("minecraft:player", 119, 83, 29, 60)
                .crop(BLOCK + "lava_still.png", 117, 264, 132, 30, 0, 0, 1, 1F / 20);
        cross(b, 43, 244, 40);
        b.entity("minecraft:player", 450, 114, 29, 60).item("minecraft:iron_pickaxe", 474, 141, 24)
                .item("minecraft:torch", 388, 54, 31).arrow(480, 219, 359, 98, 4, GREEN)
                .line(333, 276, 347, 289, 5, GREEN).line(347, 289, 376, 254, 5, GREEN);
        return b.build();
    }
    private static LiveScene ironDoor() {
        var b = scene(true);
        b.item("minecraft:stone", 32, 117, 67).arrow(110, 150, 160, 150, 4, WHITE)
                .item("minecraft:stone_button", 169, 120, 62).arrow(241, 150, 293, 150, 4, ORANGE);
        for (int row = 0; row < 4; row++) for (int col = 0; col < 5; col++)
            if (col != 2 || row == 0 || row == 3) tile(b, "stone_bricks", 315 + col * 48, 62 + row * 48, 48);
        b.texture(BLOCK + "iron_door_top.png", 411, 110, 48, 48)
                .texture(BLOCK + "iron_door_bottom.png", 411, 158, 48, 48)
                .item("minecraft:stone_button", 370, 135, 28).outline(368, 133, 32, 32, ORANGE);
        return b.build();
    }
    private static LiveScene silverfish() {
        return scene(true).item("minecraft:stone_bricks", 24, 116, 90).item("minecraft:mossy_stone_bricks", 134, 116, 90)
                .item("minecraft:cracked_stone_bricks", 244, 116, 90).arrow(353, 160, 430, 160, 5, ORANGE)
                .entity("minecraft:silverfish", 464, 119, 90, 70).outline(449, 76, 120, 180, RED).build();
    }
    private static LiveScene torchRoute() {
        var b = scene(true);
        // The same corridor seen from opposite directions: torches change visual side.
        for (int panel = 0; panel < 2; panel++) {
            int x = panel * 300;
            for (int row = 0; row < 7; row++) for (int col = 0; col < 6; col++)
                tile(b, "stone", x + 18 + col * 44, 28 + row * 39, 44);
            b.fill(x + 82, 28, 136, 273, DARK);
            for (int row = 0; row < 3; row++) b.item("minecraft:torch", x + (panel == 0 ? 214 : 42), 58 + row * 67, 43);
            b.entity("minecraft:player", x + 127, 127, 52, 112);
            if (panel == 0) b.arrow(x + 151, 105, x + 151, 45, 4, WHITE);
            else b.arrow(x + 151, 249, x + 151, 294, 4, WHITE);
            label(b, panel == 0 ? "outbound" : "return", x + 28, 311, 245);
        }
        return b.build();
    }
    private static LiveScene trading() {
        // Exact 276x166 visible vanilla merchant interface at 2x; source atlas is 512x256.
        var b = scene(true).crop("minecraft:textures/gui/container/villager.png", 24, 4, 552, 332, 0, 0, 276F / 512, 166F / 256);
        String sprites = "minecraft:textures/gui/sprites/container/villager/";
        b.outline(34, 40, 166, 40, WHITE).item("minecraft:wheat", 44, 44, 32)
                .texture(sprites + "trade_arrow.png", 108, 50, 20, 18).item("minecraft:emerald", 156, 44, 32)
                .text("20", 66, 64, 26, 14)
                .texture(sprites + "experience_bar_background.png", 296, 38, 204, 10)
                .crop(sprites + "experience_bar_current.png", 296, 38, 72, 10, 0, 0, 36F / 102, 1)
                .item("minecraft:wheat", 296, 76, 32).item("minecraft:emerald", 456, 72, 32)
                .text("20", 314, 98, 26, 14).outline(290, 70, 44, 46, ORANGE).outline(450, 66, 56, 56, ORANGE);
        return b.build();
    }
}
