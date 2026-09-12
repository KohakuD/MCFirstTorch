package ch.minenox.firsttorch.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

/** References installed GUI textures; no Minecraft texture bytes are bundled. */
final class LiveBrewingRenderer {
    private static final ResourceLocation GUI = ResourceLocation.parse("minecraft:textures/gui/container/brewing_stand.png");
    private static final ResourceLocation FUEL = ResourceLocation.parse("minecraft:textures/gui/sprites/container/brewing_stand/fuel_length.png");

    private LiveBrewingRenderer() {}

    static void draw(GuiGraphics graphics, FirstTorchLayout.Rect bounds, LiveBrewingCatalog.Step step) {
        graphics.pose().pushPose();
        graphics.pose().translate(bounds.x(), bounds.y(), 0);
        graphics.pose().scale(bounds.width() / 300F, bounds.height() / 169F, 1F);
        FirstTorchTheme.frame(graphics, new FirstTorchLayout.Rect(0, 0, 300, 169), false);
        graphics.pose().translate(62, 43, 0);
        // Functional top of the original 256x256 GUI, without unrelated player inventory rows.
        LiveRenderCompat.blit(graphics, GUI, 0, 0, 176, 83, 0F, 176F / 256F, 0F, 83F / 256F);
        LiveRenderCompat.blit(graphics, FUEL, 60, 44, 78, 48, 0F, 1F, 0F, 1F);
        graphics.renderItem(QuestIcons.resolveItem(step.ingredient()), 79, 17);
        ItemStack bottle = new ItemStack(Items.POTION);
        bottle.set(DataComponents.POTION_CONTENTS, new PotionContents(switch (step.inputPotion()) {
            case "water" -> Potions.WATER;
            case "awkward" -> Potions.AWKWARD;
            case "fire_resistance" -> Potions.FIRE_RESISTANCE;
            default -> throw new IllegalArgumentException("Unsupported brewing illustration potion");
        }));
        graphics.renderItem(bottle, 56, 51);
        graphics.renderItem(bottle, 79, 58);
        graphics.renderItem(bottle, 102, 51);
        graphics.pose().popPose();
    }
}
