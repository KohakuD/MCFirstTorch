package ch.minenox.firsttorch.client;

import java.util.LinkedHashMap;
import java.util.Map;

/** Language-neutral Redstone plans rendered from installed item and block-model assets. */
final class LiveRedstoneScenes {
    private static final int W = 1672, H = 941;
    private static final int GOLD = 0xFFF5BF53, LINE = 0xFF76797A, FRAME = 0xFF0C0D0E;
    private static final String LAMP_OFF = "minecraft:textures/block/redstone_lamp.png";
    private static final String LAMP_ON = "minecraft:textures/block/redstone_lamp_on.png";
    private static final String COBBLE = "minecraft:textures/block/cobblestone.png";

    private LiveRedstoneScenes() {}

    static Map<String, LiveScene> scenes() {
        Map<String, LiveScene> scenes = new LinkedHashMap<>();
        put(scenes, "redstone_short_line", shortLine());
        put(scenes, "redstone_dust_limit", dustLimit());
        put(scenes, "redstone_inputs", inputs());
        put(scenes, "repeater_direction", repeaterDirection());
        put(scenes, "repeater_range", repeaterRange());
        put(scenes, "repeater_delay", repeaterDelay());
        put(scenes, "comparator_read", comparatorRead());
        put(scenes, "comparator_states", comparatorStates());
        put(scenes, "piston_push", pistonPush());
        put(scenes, "piston_return", pistonReturn());
        put(scenes, "observer_orientation", observerOrientation());
        put(scenes, "observer_pulse", observerPulse());
        put(scenes, "iron_door_plan", ironDoorPlan());
        return Map.copyOf(scenes);
    }

    private static void put(Map<String, LiveScene> scenes, String name, LiveScene scene) {
        scenes.put("firsttorch:textures/questpics/" + name + ".png", scene);
    }

    private static LiveScene shortLine() {
        var b = scene();
        signalRow(b, 145, "A", new int[] {0, 0, 0}, false, false);
        signalRow(b, 565, "B", new int[] {15, 14, 13}, true, false);
        return b.build();
    }

    private static LiveScene dustLimit() {
        var b = scene();
        b.text("A", 150, 205, 60, 60).text("B", 150, 625, 60, 60);
        rowLabels(b, 145, new String[] {"0", "1–15", "16", "17"}, 200);
        b.item("minecraft:lever", 436, 145, 200).text("×15", 755, 305, 100, 45)
                .wire(15, 636, 145, 200).wire(0, 836, 145, 200)
                .texture(LAMP_OFF, 1036, 145, 200, 200).outline(1036, 145, 200, 200, FRAME)
                .arrow(502, 393, 1136, 393, 9, GOLD);
        rowLabels(b, 565, new String[] {"0", "1–15", "16", "17"}, 200);
        b.item("minecraft:lever", 436, 565, 200).text("×15", 755, 725, 100, 45)
                .wire(15, 636, 565, 200).texture(LAMP_ON, 836, 565, 200, 200)
                .outline(836, 565, 200, 200, FRAME).arrow(502, 813, 936, 813, 9, GOLD);
        return b.build();
    }

    private static LiveScene inputs() {
        var b = scene();
        inputRow(b, 130, "A", "minecraft:lever");
        inputRow(b, 405, "B", "minecraft:stone_button");
        inputRow(b, 680, "C", "minecraft:stone_pressure_plate");
        return b.build();
    }

    private static LiveScene repeaterDirection() {
        var b = scene();
        rowLabels(b, 235, new String[] {"0", "1", "2"}, 220);
        b.item("minecraft:lever", 506, 235, 220).blockTop("minecraft:block/repeater_1tick", 1, 726, 235, 220)
                .texture(LAMP_OFF, 946, 235, 220, 220).outline(946, 235, 220, 220, FRAME)
                .arrow(579, 523, 726, 523, 9, GOLD).arrow(799, 523, 946, 523, 9, GOLD);
        return b.build();
    }

    private static LiveScene repeaterRange() {
        var b = scene();
        rowLabels(b, 285, new String[] {"0", "1–15", "16", "17–19", "20"}, 220);
        b.item("minecraft:lever", 286, 285, 220).text("×15", 566, 445, 100, 45)
                .wire(0, 506, 285, 220).blockTop("minecraft:block/repeater_1tick", 1, 726, 285, 220)
                .text("×3", 1006, 445, 80, 45).wire(0, 946, 285, 220)
                .texture(LAMP_OFF, 1166, 285, 220, 220).outline(1166, 285, 220, 220, FRAME)
                .arrow(359, 573, 1313, 573, 9, GOLD);
        return b.build();
    }

    private static LiveScene repeaterDelay() {
        var b = scene();
        delayRow(b, 135, "A", "minecraft:block/repeater_1tick");
        delayRow(b, 565, "B", "minecraft:block/repeater_4tick");
        return b.build();
    }

    private static LiveScene comparatorRead() {
        var b = scene();
        rowLabels(b, 235, new String[] {"0", "1", "2", "3", "4"}, 180);
        b.item("minecraft:chest", 386, 235, 180).blockTop("minecraft:block/comparator", 1, 566, 235, 180)
                .wire(0, 746, 235, 180).wire(0, 926, 235, 180)
                .texture(LAMP_OFF, 1106, 235, 180, 180).outline(1106, 235, 180, 180, FRAME)
                .arrow(446, 483, 1226, 483, 9, GOLD);
        return b.build();
    }

    private static LiveScene comparatorStates() {
        var b = scene();
        comparatorState(b, 128, "A", "0", false);
        comparatorState(b, 403, "B", "64", false);
        comparatorState(b, 678, "C", "128", true);
        return b.build();
    }

    private static LiveScene pistonPush() {
        var b = scene();
        rowLabels(b, 235, new String[] {"0", "1", "2"}, 220);
        b.blockTop("minecraft:block/piston", 1, 506, 235, 220).texture(COBBLE, 726, 235, 220, 220)
                .outline(726, 235, 220, 220, FRAME).text("A", 806, 599, 60, 55)
                .crop("minecraft:textures/block/piston_top.png", 761, 665, 150, 150, 0, 0, 1, 1)
                .outline(761, 665, 150, 150, FRAME).arrow(579, 523, 726, 523, 9, GOLD);
        return b.build();
    }

    private static LiveScene pistonReturn() {
        var b = scene();
        pistonRow(b, 145, "A", "minecraft:block/piston", "minecraft:textures/block/piston_top.png", false);
        pistonRow(b, 565, "B", "minecraft:block/sticky_piston", "minecraft:textures/block/piston_top_sticky.png", true);
        return b.build();
    }

    private static LiveScene observerOrientation() {
        var b = scene();
        rowLabels(b, 170, new String[] {"0", "1", "2"}, 250);
        b.texture(COBBLE, 460, 170, 250, 250).outline(460, 170, 250, 250, FRAME)
                .blockTop("minecraft:block/observer", 3, 710, 170, 250).texture(LAMP_OFF, 960, 170, 250, 250)
                .outline(960, 170, 250, 250, FRAME).arrow(530, 468, 670, 468, 9, GOLD)
                .arrow(780, 468, 920, 468, 9, GOLD).line(200, 530, 1472, 530, 3, 0xFF646460)
                .text("A", 480, 570, 210, 50).crop("minecraft:textures/block/observer_front.png", 480, 640, 210, 210, 0, 0, 1, 1)
                .text("B", 930, 570, 210, 50).crop("minecraft:textures/block/observer_back.png", 930, 640, 210, 210, 0, 0, 1, 1);
        return b.build();
    }

    private static LiveScene observerPulse() {
        var b = scene();
        int left = 350, top = 340, size = 220;
        for (int i = 0; i < 3; i++) {
            int x = left + i * 360;
            b.text(String.valueOf((char) ('A' + i)), x, 268, size, 55)
                    .texture(i == 1 ? LAMP_ON : LAMP_OFF, x, top, size, size).outline(x, top, size, size, FRAME);
            if (i < 2) b.arrow(x + size + 35, top + size / 2, x + 325, top + size / 2, 9, GOLD);
        }
        return b.build();
    }

    private static LiveScene ironDoorPlan() {
        var b = scene();
        rowLabels(b, 290, new String[] {"0", "1", "2"}, 240);
        b.item("minecraft:stone_pressure_plate", 476, 290, 240).item("minecraft:iron_door", 716, 290, 240)
                .item("minecraft:stone_pressure_plate", 956, 290, 240).arrow(556, 650, 1116, 650, 9, GOLD)
                .arrow(1116, 690, 556, 690, 9, GOLD);
        return b.build();
    }

    private static LiveScene.Builder scene() { return new LiveScene.Builder(W, H, true); }

    private static void signalRow(LiveScene.Builder b, int top, String row, int[] powers, boolean lit, boolean unused) {
        b.text(row, 150, top + 60, 60, 60);
        rowLabels(b, top, new String[] {"0", "1", "2", "3", "4"}, 180);
        b.item("minecraft:lever", 386, top, 180);
        for (int i = 0; i < powers.length; i++) b.wire(powers[i], 566 + i * 180, top, 180);
        b.texture(lit ? LAMP_ON : LAMP_OFF, 1106, top, 180, 180).outline(1106, top, 180, 180, FRAME)
                .arrow(446, top + 228, 1226, top + 228, 9, GOLD);
    }

    private static void inputRow(LiveScene.Builder b, int top, String row, String input) {
        b.text(row, 150, top + 38, 60, 60).item(input, 265, top, 132);
        for (int i = 0; i < 3; i++) {
            int x = 565 + i * 310;
            b.texture(i == 1 ? LAMP_ON : LAMP_OFF, x, top, 132, 132).outline(x, top, 132, 132, FRAME);
            if (i < 2) b.arrow(x + 168, top + 66, x + 272, top + 66, 9, GOLD);
        }
    }

    private static void delayRow(LiveScene.Builder b, int top, String label, String model) {
        b.text(label, 180, top + 70, 60, 60); rowLabels(b, top, new String[] {"0", "1", "2", "3"}, 180);
        b.item("minecraft:lever", 476, top, 180).blockTop(model, 1, 656, top, 180).blockTop(model, 1, 836, top, 180)
                .texture(LAMP_OFF, 1016, top, 180, 180).outline(1016, top, 180, 180, FRAME)
                .arrow(536, top + 228, 1136, top + 228, 9, GOLD);
    }

    private static void comparatorState(LiveScene.Builder b, int top, String label, String count, boolean on) {
        b.text(label, 150, top + 45, 60, 60).texture(COBBLE, 350, top, 150, 150).outline(350, top, 150, 150, FRAME)
                .text(count, 515, top + 43, 130, 65).arrow(670, top + 75, 860, top + 75, 9, GOLD)
                .texture(on ? LAMP_ON : LAMP_OFF, 885, top, 150, 150).outline(885, top, 150, 150, FRAME);
    }

    private static void pistonRow(LiveScene.Builder b, int top, String label, String model, String front, boolean blockAtOne) {
        b.text(label, 150, top + 60, 60, 60); rowLabels(b, top, new String[] {"0", "1", "2"}, 180);
        b.blockTop(model, 1, 566, top, 180);
        b.texture(COBBLE, blockAtOne ? 746 : 926, top, 180, 180).outline(blockAtOne ? 746 : 926, top, 180, 180, FRAME)
                .crop(front, 1300, top + 25, 120, 120, 0, 0, 1, 1).outline(1300, top + 25, 120, 120, FRAME);
    }

    private static void rowLabels(LiveScene.Builder b, int top, String[] labels, int size) {
        int left = (W - labels.length * size) / 2;
        for (int i = 0; i < labels.length; i++) b.text(labels[i], left + i * size, top - 73, size, 55);
    }
}
