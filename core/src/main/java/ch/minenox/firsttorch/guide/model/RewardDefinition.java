package ch.minenox.firsttorch.guide.model;

/** Declarative rewards only; experience amounts represent points, never levels. */
public record RewardDefinition(String id, Type type, String itemId, int amount) {
    public enum Type {
        EXPERIENCE,
        ITEM
    }
}
