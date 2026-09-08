package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.model.QuestDefinition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Uses the target game's item renderer, with a visible fallback for missing optional assets. */
final class QuestIcons {
    private QuestIcons() {}

    static ItemStack resolve(QuestDefinition quest) {
        String id = quest.iconItemId();
        if (id == null) id = quest.tasks().stream().filter(t -> t.type() == ch.minenox.firsttorch.guide.model.TaskDefinition.Type.INVENTORY
                        && t.itemId() != null)
                .map(t -> t.itemId()).findFirst().orElse("minecraft:book");
        return resolveItem(id);
    }

    static ItemStack resolveItem(String id) {
        if (id == null) return new ItemStack(Items.BOOK);
        var item = BuiltInRegistries.ITEM.getValue(Identifier.parse(id));
        return new ItemStack(item == null || item == Items.AIR ? Items.BOOK : item);
    }
}
