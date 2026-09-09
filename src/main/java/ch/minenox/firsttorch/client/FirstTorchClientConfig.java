package ch.minenox.firsttorch.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class FirstTorchClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue CHAPTER_FIREWORKS = BUILDER
            .comment("Show a brief UI firework and sound for newly completed chapters.")
            .define("chapterFireworks", true);
    public static final ModConfigSpec.BooleanValue ENLARGED_VIEW = BUILDER
            .comment("Enlarge quest-book text and controls by up to 25 percent, limited by available window space.")
            .define("enlargedView", false);
    public static final ModConfigSpec SPEC = BUILDER.build();
    private FirstTorchClientConfig() {}
}
