package ch.minenox.firsttorch.client;

import java.util.LinkedHashMap;
import java.util.Map;

/** Native scene families share dispatch and a single packaging coverage contract. */
final class LiveSceneCatalog {
    private static final Map<String, LiveScene> SCENES = create();
    private LiveSceneCatalog() {}
    private static Map<String, LiveScene> create() {
        var scenes = new LinkedHashMap<String, LiveScene>();
        merge(scenes, LiveRedstoneScenes.scenes());
        merge(scenes, LiveEndScenes.scenes());
        merge(scenes, LiveOverworldScenes.scenes());
        scenes.put("firsttorch:textures/questpics/bastion_remnant.png", LiveBastionScene.create());
        return Map.copyOf(scenes);
    }
    private static void merge(Map<String, LiveScene> all, Map<String, LiveScene> family) {
        family.forEach((key, value) -> { if (all.putIfAbsent(key, value) != null) throw new IllegalStateException("Duplicate scene " + key); });
    }
    static LiveScene find(String resource) { return SCENES.get(resource); }
    static Map<String, LiveScene> scenes() { return SCENES; }
}
