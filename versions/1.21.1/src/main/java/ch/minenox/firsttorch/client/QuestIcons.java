package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Uses the target game's item renderer, with a visible fallback for missing optional assets. */
final class QuestIcons {
    private QuestIcons() {}

    static ItemStack resolveTask(TaskDefinition task, QuestDefinition quest) {
        return switch (task.type()) {
            case MANUAL -> new ItemStack(Items.BOOK);
            case ADVANCEMENT -> resolve(quest);
            case INVENTORY -> resolveItem(task.itemId());
            case INVENTORY_TAG -> {
                var tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(task.itemId()));
                // Choose a real member of the synced tag, never the unrelated chapter/quest icon.
                var representative = BuiltInRegistries.ITEM.stream()
                        .filter(item -> item != Items.AIR && item.builtInRegistryHolder().is(tag))
                        .findFirst().orElse(Items.BOOK);
                yield new ItemStack(representative);
            }
        };
    }

    static ItemStack resolve(QuestDefinition quest) {
        String id = quest.iconItemId();
        if (id == null) id = quest.tasks().stream().filter(t -> t.type() == ch.minenox.firsttorch.guide.model.TaskDefinition.Type.INVENTORY
                        && t.itemId() != null)
                .map(t -> t.itemId()).findFirst().orElse("minecraft:book");
        return resolveItem(id);
    }

    static ItemStack resolveItem(String id) {
        if (id == null) return new ItemStack(Items.BOOK);
        var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
        return new ItemStack(item == null || item == Items.AIR ? Items.BOOK : item);
    }
}
