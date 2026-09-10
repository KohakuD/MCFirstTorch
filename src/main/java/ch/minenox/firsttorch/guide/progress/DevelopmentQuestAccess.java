package ch.minenox.firsttorch.guide.progress;

import java.util.UUID;

/** Retired test access: launch properties must never enable completion bypasses. */
public final class DevelopmentQuestAccess {
    public static final String PROPERTY = "firsttorch.testMode";
    private DevelopmentQuestAccess() {}

    public static boolean enabled() { return false; }

    public static boolean allowed(boolean enabled, boolean integrated, UUID owner, UUID player) {
        return enabled && integrated && owner != null && owner.equals(player);
    }
}
