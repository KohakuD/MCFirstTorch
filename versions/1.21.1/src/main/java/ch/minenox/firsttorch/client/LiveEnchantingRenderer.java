package ch.minenox.firsttorch.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Installed vanilla GUI and items; offers are schematic, not promised enchantments. */
final class LiveEnchantingRenderer {
    private static final ResourceLocation GUI = ResourceLocation.parse("minecraft:textures/gui/container/enchanting_table.png");
    private static final ResourceLocation OFFER = sprite("enchantment_slot");
    private LiveEnchantingRenderer() {}
    private static ResourceLocation sprite(String name) {
        return ResourceLocation.parse("minecraft:textures/gui/sprites/container/enchanting_table/" + name + ".png");
    }
    static void draw(GuiGraphics graphics, FirstTorchLayout.Rect bounds) {
        graphics.pose().pushPose();
        graphics.pose().translate(bounds.x(), bounds.y(), 0);
        graphics.pose().scale(bounds.width() / 176F, bounds.height() / 84F, 1F);
        LiveRenderCompat.blit(graphics, GUI, 0, 0, 176, 84, 0F, 176F / 256F, 0F, 84F / 256F);
        graphics.renderItem(QuestIcons.resolveItem("minecraft:iron_pickaxe"), 15, 47);
        graphics.renderItem(QuestIcons.resolveItem("minecraft:lapis_lazuli"), 35, 47);
        for (int row = 0; row < 3; row++) {
            int top = 14 + row * 19;
            LiveRenderCompat.blit(graphics, OFFER, 60, top, 168, top + 19, 0F, 1F, 0F, 1F);
            LiveRenderCompat.blit(graphics, sprite("level_" + (row + 1)), 61, top + 1, 77, top + 17, 0F, 1F, 0F, 1F);
        }
        for (int index = 1; index <= 3; index++) {
            var region = LiveEnchantingLayout.region(index);
            graphics.renderOutline(region.x(), region.y(), region.width(), region.height(), FirstTorchTheme.GOLD);
            int labelX = index == 3 ? 106 : region.x() + 2;
            int labelY = index == 3 ? 1 : 66;
            graphics.fill(labelX, labelY, labelX + 14, labelY + 11, FirstTorchTheme.BACKGROUND);
            LiveRenderCompat.text(graphics, Component.literal(Integer.toString(index)),
                    labelX, labelX + 14, labelY, labelY + 11);
        }
        graphics.pose().popPose();
    }
}
