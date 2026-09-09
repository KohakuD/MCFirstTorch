package ch.minenox.firsttorch.client;

/** Short state announcement before the existing lesson description. */
final class QuestNarrationState {
    private QuestNarrationState() {}

    static String key(boolean available, boolean locked, boolean completed, boolean claimable) {
        if (!available) return "screen.firsttorch.narration.unavailable";
        if (locked) return completed ? "screen.firsttorch.narration.completed_locked"
                : "screen.firsttorch.narration.locked";
        if (completed) return claimable ? "screen.firsttorch.narration.reward"
                : "screen.firsttorch.narration.completed";
        return "screen.firsttorch.narration.open";
    }
}
