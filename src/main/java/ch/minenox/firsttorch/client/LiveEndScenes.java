package ch.minenox.firsttorch.client;

import java.util.LinkedHashMap;
import java.util.Map;

/** End lesson diagrams composed from target-version resources at render time. */
final class LiveEndScenes {
    private static final int ORANGE = 0xFFFF9100;
    private static final int RED = 0xFFE13030;
    private static final int PANEL = 0xFF232328;
    private static final int LINE = 0xFF706D78;

    private LiveEndScenes() {}

    static Map<String, LiveScene> scenes() {
        var scenes = new LinkedHashMap<String, LiveScene>();
        add(scenes, "end_crystal_exposed", exposedCrystal());
        add(scenes, "end_crystal_removal", crystalRemoval());
        add(scenes, "end_portal_final_eye", finalEye());
        add(scenes, "end_portal_frame_states", portalFrames());
        add(scenes, "end_portal_room", portalRoom());
        add(scenes, "ender_dragon_flight", dragonFlight());
        add(scenes, "ender_dragon_perched", dragonPerched());
        add(scenes, "ender_eye_search", eyeSearch());
        add(scenes, "enderman_roof_shelter", endermanShelter());
        add(scenes, "outer_end_arrival", outerArrival());
        add(scenes, "dragon_egg_retrieval", eggRetrieval());
        add(scenes, "chorus_fruit_safety", chorusSafety());
        add(scenes, "shulker_levitation", shulkerLevitation());
        add(scenes, "elytra_water_course", elytraCourse());
        return Map.copyOf(scenes);
    }

    private static void add(Map<String, LiveScene> scenes, String name, LiveScene scene) {
        scenes.put("firsttorch:textures/questpics/" + name + ".png", scene);
    }
    private static LiveScene.Builder base(boolean wide) {
        return new LiveScene.Builder(600, 340, wide).fill(0, 0, 600, 340, 0xFF14131A)
                .outline(4, 4, 592, 332, LINE);
    }
    private static LiveScene.Builder panel(LiveScene.Builder b, int x, int y, int w, int h) {
        return b.fill(x, y, w, h, PANEL).outline(x, y, w, h, LINE);
    }
    private static LiveScene.Builder floor(LiveScene.Builder b, int x, int y, int count, int size) {
        for (int i = 0; i < count; i++) b.item("minecraft:end_stone", x + i * size, y, size);
        return b;
    }
    private static LiveScene.Builder pillar(LiveScene.Builder b, int x, int y, int count, int size) {
        for (int i = 0; i < count; i++) b.item("minecraft:obsidian", x, y - i * size, size);
        return b;
    }
    // Orthographic world plans and cutaways use exact face textures, preserving the block grid.
    private static void tile(LiveScene.Builder b, String block, int x, int y, int size) {
        b.texture("minecraft:textures/block/" + block + ".png", x, y, size, size)
                .outline(x, y, size, size, LINE);
    }
    private static void step(LiveScene.Builder b, int number, int x) {
        b.text(Integer.toString(number), x, 38, 24, 24);
    }
    private static void gateway(LiveScene.Builder b, int x, int y, int cell) {
        for (int row : new int[] {-2, -1, 1, 2}) tile(b, "bedrock", x, y + row * cell, cell);
        for (int row : new int[] {-2, 2}) for (int col : new int[] {-1, 1})
            tile(b, "bedrock", x + col * cell, y + row * cell, cell);
        b.texture("minecraft:textures/entity/end_portal/end_portal.png", x, y, cell, cell);
    }

    private static LiveScene exposedCrystal() {
        var b = base(false); floor(b, 20, 278, 12, 46); pillar(b, 430, 246, 5, 42);
        return b.item("minecraft:end_crystal", 418, 0, 68).item("minecraft:bow", 78, 190, 72)
                .arrow(142, 215, 440, 57, 8, ORANGE).build();
    }
    private static LiveScene crystalRemoval() {
        var b = base(true); panel(b, 16, 25, 274, 285); panel(b, 310, 25, 274, 285);
        for (int p = 0; p < 2; p++) {
            int left = 16 + p * 294, cx = left + 208;
            step(b, p + 1, left + 10);
            for (int x = left + 12; x < left + 246; x += 26) tile(b, "end_stone", x, 272, 26);
            for (int y = 132; y < 272; y += 28) tile(b, "obsidian", cx - 14, y, 28);
            b.item("minecraft:end_crystal", cx - 23, 80, 46);
            b.texture("minecraft:textures/block/iron_bars.png", cx - 36, 65, 10, 69)
                    .texture("minecraft:textures/block/iron_bars.png", cx + 27, 65, 10, 69)
                    .texture("minecraft:textures/block/iron_bars.png", cx - 36, 65, 73, 10)
                    .texture("minecraft:textures/block/iron_bars.png", cx - 36, 124, 73, 10)
                    .outline(cx - 39, 86, 17, 29, 0xFFFFFFFF);
            if (p == 0) {
                for (int s = 0; s < 6; s++) tile(b, "end_stone", left + 48 + s * 23, 244 - s * 28, 28);
                b.item("minecraft:iron_pickaxe", left + 128, 66, 36)
                        .arrow(left + 152, 103, cx - 30, 103, 4, ORANGE);
            } else {
                b.fill(cx - 36, 89, 10, 23, PANEL).item("minecraft:bow", left + 27, 218, 44)
                        .arrow(left + 64, 230, cx - 30, 105, 5, ORANGE);
            }
        }
        return b.build();
    }
    private static LiveScene finalEye() {
        var b = base(false);
        for (int r = 0; r < 6; r++) for (int c = 0; c < 6; c++) tile(b, "stone_bricks", 120+c*45, 30+r*45, 45);
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++)
            b.crop("minecraft:textures/block/lava_still.png",210+c*45,120+r*45,45,45,0,0,1,.05f);
        for (int i = 0; i < 3; i++) {
            b.blockTop(i == 2 ? "minecraft:end_portal_frame" : "minecraft:end_portal_frame_filled", 2, 210+i*45,75,45)
                    .blockTop("minecraft:end_portal_frame_filled",0,210+i*45,255,45)
                    .blockTop("minecraft:end_portal_frame_filled",1,165,120+i*45,45)
                    .blockTop("minecraft:end_portal_frame_filled",3,345,120+i*45,45);
        }
        return b.item("minecraft:ender_eye",460,125,45).arrow(460,148,341,98,5,ORANGE)
                .outline(300,75,45,45,ORANGE).outline(124,214,37,37,ORANGE).build();
    }
    private static LiveScene portalFrames() {
        var b = base(true); panel(b, 35, 55, 230, 220); panel(b, 335, 55, 230, 220);
        return b.blockTop("minecraft:end_portal_frame",0,103,110,100).blockTop("minecraft:end_portal_frame_filled",0,403,110,100)
                .arrow(275,165,325,165,6,ORANGE).build();
    }
    private static LiveScene portalRoom() {
        var b=base(false);
        for(int r=0;r<8;r++) for(int c=0;c<11;c++) tile(b,"stone_bricks",80+c*35,25+r*35,35);
        for(int r=0;r<3;r++) for(int c=0;c<3;c++) b.crop("minecraft:textures/block/lava_still.png",220+c*35,95+r*35,35,35,0,0,1,.05f);
        for(int i=0;i<3;i++) b.blockTop("minecraft:end_portal_frame",2,220+i*35,60,35)
                .blockTop("minecraft:end_portal_frame",0,220+i*35,200,35)
                .blockTop("minecraft:end_portal_frame",1,185,95+i*35,35)
                .blockTop("minecraft:end_portal_frame",3,325,95+i*35,35);
        return b.item("minecraft:spawner",255,252,40).outline(249,246,52,52,ORANGE).build();
    }
    private static LiveScene dragonFlight() {
        var b = base(false); floor(b, 25, 280, 12, 46);
        return b.entity("minecraft:ender_dragon", 280, 35, 255, 155).item("minecraft:bow", 45, 180, 72)
                .arrow(112, 210, 320, 105, 7, ORANGE).texture("minecraft:textures/entity/enderdragon/dragon_fireball.png", 175, 210, 45, 45)
                .fill(145,250,115,30,0xFF6C0D95).outline(145,250,115,30,0xFFDE59FF)
                .arrow(355,302,455,302,6,ORANGE).arrow(355,302,275,302,6,ORANGE).build();
    }
    private static LiveScene dragonPerched() {
        var b = base(false); floor(b, 25, 280, 12, 46);
        for(int row=0;row<3;row++) for(int col=0;col<7-row*2;col++) tile(b,"bedrock",195+row*30+col*30,250-row*30,30);
        tile(b,"bedrock",285,130,30); tile(b,"bedrock",285,160,30);
        return b.entity("minecraft:ender_dragon",140,50,300,165)
                .item("minecraft:bow", 40, 75, 62).line(40, 75, 102, 137, 8, RED).line(102, 75, 40, 137, 8, RED)
                .item("minecraft:iron_sword", 470, 215, 62).arrow(500,220,350,175,7,ORANGE)
                .arrow(428,250,428,190,6,ORANGE).build();
    }
    private static LiveScene eyeSearch() {
        var b = base(true); panel(b, 18, 35, 170, 260); panel(b, 215, 35, 170, 260); panel(b, 412, 35, 170, 260);
        step(b,1,28); step(b,2,225); step(b,3,422);
        return b.item("minecraft:ender_eye", 82, 150, 45).arrow(105, 145, 135, 90, 6, ORANGE)
                .item("minecraft:ender_eye", 275, 94, 45).arrow(310, 116, 355, 116, 6, ORANGE)
                .item("minecraft:ender_eye", 475, 105, 45).arrow(498, 145, 498, 205, 6, ORANGE)
                .texture("minecraft:textures/block/stone_bricks.png", 440, 230, 115, 50).build();
    }
    private static LiveScene endermanShelter() {
        var b = base(true); panel(b, 18, 35, 270, 260); panel(b, 312, 35, 270, 260);
        for (int x = 65; x < 215; x += 50) for (int y = 85; y < 235; y += 50) tile(b, "end_stone", x, y, 50);
        for (int y = 85; y < 235; y += 50) tile(b, "obsidian", 222, y, 50);
        for (int x = 325; x < 575; x += 50) tile(b, "end_stone", x, 270, 50);
        for (int x = 335; x < 485; x += 50) tile(b, "end_stone", x, 120, 50);
        for (int y = 70; y < 270; y += 50) tile(b, "obsidian", 320, y, 50);
        return b.outline(120,140,40,40,ORANGE).entity("minecraft:player",410,180,46,90)
                .entity("minecraft:enderman",515,125,46,145)
                .line(390,170,390,270,3,0xFFFFFFFF).line(382,170,398,170,3,0xFFFFFFFF)
                .line(382,220,398,220,3,0xFFFFFFFF).line(382,270,398,270,3,0xFFFFFFFF)
                .text("2",375,208,15,18).line(516,118,558,160,6,RED).line(558,118,516,160,6,RED).build();
    }
    private static LiveScene outerArrival() {
        var b = base(false);
        for (int y = 235; y < 325; y += 30) for (int x = 55; x < 565; x += 30) tile(b,"end_stone",x,y,30);
        gateway(b,125,145,30); tile(b,"cobblestone",245,205,30); tile(b,"cobblestone",245,175,30);
        return b.outline(88,75,104,165,ORANGE).item("minecraft:torch",245,135,30)
                .arrow(200,220,235,220,5,ORANGE).arrow(290,220,470,220,5,ORANGE).build();
    }
    private static LiveScene eggRetrieval() {
        var b = base(true); panel(b, 15, 35, 180, 260); panel(b, 210, 35, 180, 260); panel(b, 405, 35, 180, 260);
        step(b,1,25); step(b,2,220); step(b,3,415);
        tile(b,"end_stone",80,225,55); tile(b,"end_stone",275,180,55);
        return b.item("minecraft:dragon_egg",78,170,58).item("minecraft:dragon_egg",140,105,42)
                .arrow(130,173,158,148,5,ORANGE).item("minecraft:dragon_egg",273,124,58)
                .item("minecraft:torch",283,239,38).outline(269,175,67,108,0xFFFFFFFF)
                .outline(470,180,55,55,ORANGE).line(474,184,521,231,4,ORANGE).line(521,184,474,231,4,ORANGE)
                .item("minecraft:dragon_egg",474,204,46).item("minecraft:torch",478,239,38)
                .arrow(497,137,497,202,5,ORANGE).build();
    }
    private static LiveScene chorusSafety() {
        var b = base(false);
        for (int r=0;r<17;r++) for (int c=0;c<17;c++) tile(b,"end_stone",185+c*16,30+r*16,16);
        b.outline(193,38,256,256,ORANGE).text("8",382,169,18,20)
                .arrow(321,166,433,166,4,ORANGE).arrow(321,166,237,109,4,ORANGE).arrow(321,166,265,247,4,ORANGE);
        return b.item("minecraft:chorus_fruit",70,136,54).arrow(130,166,175,166,5,ORANGE)
                .crop("minecraft:textures/entity/player/wide/steve.png",307,152,28,28,.125f,.125f,.25f,.25f)
                .line(155,37,179,61,5,RED).line(179,37,155,61,5,RED)
                .line(463,275,487,299,5,RED).line(487,275,463,299,5,RED).build();
    }
    private static LiveScene shulkerLevitation() {
        var b = base(true); panel(b, 18, 35, 270, 260); panel(b, 312, 35, 270, 260);
        shulkerFront(b,65,240,false); shulkerFront(b,185,240,true);
        return b
                .arrow(145,160,205,160,6,ORANGE).crop("minecraft:textures/entity/shulker/spark.png",330,100,44,44,.03125f,.0625f,.15625f,.3125f)
                .line(375,122,409,122,5,ORANGE).line(409,122,409,202,5,ORANGE).arrow(409,202,451,202,5,ORANGE)
                .fill(453,161,12,96,LINE).texture("minecraft:textures/mob_effect/levitation.png",510,80,36,36)
                .arrow(528,127,528,171,5,ORANGE).item("minecraft:milk_bucket",475,195,36).item("minecraft:water_bucket",530,195,36)
                .outline(471,190,99,48,LINE).build();
    }
    private static void shulkerFront(LiveScene.Builder b,int x,int floorY,boolean open) {
        // Exact ShulkerModel front UVs: lid16x12, base16x8 and head6x6 on a64x64 atlas.
        String path="minecraft:textures/entity/shulker/shulker.png";
        b.crop(path,x,floorY-32,64,32,.25f,.6875f,.5f,.8125f)
                .crop(path,x,floorY-80-(open?34:0),64,48,.25f,.25f,.5f,.4375f);
        if(open) b.crop(path,x+20,floorY-64,24,24,.09375f,.90625f,.1875f,1);
    }
    private static LiveScene elytraCourse() {
        var b = base(true); panel(b, 18, 30, 270, 280); panel(b, 312, 30, 270, 280);
        for (int r=0;r<12;r++) for (int c=0;c<9;c++) b.tintedCrop("minecraft:textures/block/water_still.png",55+c*18,50+r*18,18,18,0,0,1,.03125f,0xFF3F76E4);
        for (int r=0;r<4;r++) for (int c=0;c<5;c++) {
            tile(b,"cobblestone",91+c*18,50+r*18,18);
            if (r==0 || c==0 || c==4 || r==3 && c==1) b.outline(92+c*18,51+r*18,16,16,0xFF08080A);
        }
        b.outline(129,106,14,14,ORANGE).outline(147,106,14,14,ORANGE)
                .item("minecraft:elytra",128,82,25).arrow(145,119,145,244,5,ORANGE);
        for (int c=0;c<8;c++) b.tintedCrop("minecraft:textures/block/water_still.png",330+c*28,250,28,28,0,0,1,.03125f,0xFF3F76E4);
        for (int c=0;c<4;c++) tile(b,"cobblestone",330+c*28,138,28);
        for (int r=0;r<4;r++) { tile(b,"cobblestone",358,138+r*28,28); b.texture("minecraft:textures/block/ladder.png",361,141+r*28,22,22); }
        tile(b,"cobblestone",330,110,28);
        for (int r=0;r<4;r++) b.fill(551,141+r*28,7,22,ORANGE);
        return b.item("minecraft:elytra",390,112,26).arrow(418,149,533,255,5,ORANGE).build();
    }
}
