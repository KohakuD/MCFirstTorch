package ch.minenox.firsttorch.guide.model;

/** Declarative requirements only; this definition does not track or complete tasks. */
public record TaskDefinition(String id, Type type, String itemId, int count, String titleKey,
        String advancementId, String criterion) {
    public TaskDefinition(String id, Type type, String itemId, int count, String titleKey) {
        this(id, type, itemId, count, titleKey, null, null);
    }
    public TaskDefinition(String id, Type type, String itemId, int count) {
        this(id, type, itemId, count, null);
    }
    public enum Type {
        MANUAL,
        INVENTORY,
        INVENTORY_TAG,
        ADVANCEMENT
    }

    /** Key used by inventory observations; tags are deliberately separate from item identifiers. */
    public String inventoryKey() {
        if (type == Type.ADVANCEMENT) return "@" + advancementId + "|" + (criterion == null ? "" : criterion);
        return type == Type.INVENTORY_TAG ? "#" + itemId : itemId;
    }

    public boolean automatic() { return type != Type.MANUAL; }
}
