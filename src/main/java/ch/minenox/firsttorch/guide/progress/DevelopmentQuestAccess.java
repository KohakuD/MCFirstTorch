package ch.minenox.firsttorch.guide.progress;

import java.util.UUID;

/** Explicit launch opt-in, limited to the integrated server's owning player. */
public final class DevelopmentQuestAccess {
    public static final String PROPERTY = "firsttorch.testMode";
    private DevelopmentQuestAccess() {}

    public static boolean enabled() { return Boolean.getBoolean(PROPERTY); }

    public static boolean allowed(boolean enabled, boolean integrated, UUID owner, UUID player) {
        return enabled && integrated && owner != null && owner.equals(player);
    }
}
