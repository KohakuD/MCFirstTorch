package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/** Presentation-only sample cards; never reads inventory or grants rewards. */
final class PreviewDetailsRenderer {
    private PreviewDetailsRenderer() {}

    static void draw(GuiGraphicsExtractor graphics, Font font, Rect panel, QuestDefinition quest) {
        if (quest == null) return;
        int availableWidth = Math.max(1, panel.width() - 22);
        float scale = Math.min(1F, Math.min(availableWidth / 210F, Math.max(1, panel.height() - 22) / 266F));
        int width = Math.max(210, (int) (availableWidth / scale));
        graphics.pose().pushMatrix();
        graphics.pose().translate(panel.x() + 11, panel.y() + 11);
        graphics.pose().scale(scale, scale);
        FirstTorchTheme.medallion(graphics, new Rect(1, 3, 46, 46), true, false);
        item(graphics, DesignPreview.itemId(quest.id()), 10, 12, 28);
        wrapped(graphics, font, Component.translatable(quest.titleKey()).withStyle(style -> style.withBold(true)),
                58, 5, width - 58, 2, FirstTorchTheme.TEXT);
        wrapped(graphics, font, Component.translatable(quest.descriptionKey()),
                58, 29, width - 58, 3, FirstTorchTheme.MUTED);
        label(graphics, "screen.firsttorch.preview.task", 0, 70);
        FirstTorchTheme.inset(graphics, new Rect(0, 84, width, 45));
        if (DesignPreview.completed(quest.id())) {
            FirstTorchTheme.completionBadge(graphics, 10, 97);
        } else {
            FirstTorchTheme.medallion(graphics, new Rect(8, 95, 20, 20), false, false);
        }
        wrapped(graphics, font, Component.translatable(quest.descriptionKey()),
                36, 91, width - 45, 3, FirstTorchTheme.TEXT);
        boolean completed = DesignPreview.completed(quest.id());
        graphics.textRenderer().accept(0, 136, color(Component.translatable("screen.firsttorch.progress", completed ? 1 : 0, 1), FirstTorchTheme.MUTED));
        graphics.fill(0, 150, width, 155, 0xFF080A0B);
        if (completed) graphics.fill(1, 151, width - 1, 154, FirstTorchTheme.GOLD);
        label(graphics, "screen.firsttorch.preview.requirements", 0, 165);
        List<DesignPreview.Requirement> requirements = DesignPreview.requirements(quest.id());
        int gap = 4;
        int cardWidth = (width - gap * (requirements.size() - 1)) / requirements.size();
        for (int index = 0; index < requirements.size(); index++) {
            DesignPreview.Requirement requirement = requirements.get(index);
            int x = index * (cardWidth + gap);
            FirstTorchTheme.inset(graphics, new Rect(x, 179, cardWidth, 32));
            item(graphics, requirement.itemId(), x + 6, 185, 20);
            graphics.textRenderer().accept(x + 31, 191,
                    color(Component.literal((completed ? requirement.count() : 0) + "/" + requirement.count()), FirstTorchTheme.TEXT));
        }
        label(graphics, "screen.firsttorch.preview.reward", 0, 222);
        FirstTorchTheme.inset(graphics, new Rect(0, 236, width, 30));
        item(graphics, "minecraft:experience_bottle", 8, 241, 20);
        graphics.textRenderer().accept(35, 247, color(Component.translatable("screen.firsttorch.preview.reward.xp", 15), FirstTorchTheme.TEXT));
        int divider = Math.max(96, width / 2);
        graphics.fill(divider, 242, divider + 1, 260, 0xFF666057);
        item(graphics, "minecraft:bread", divider + 12, 241, 20);
        graphics.textRenderer().accept(divider + 40, 247, color(Component.literal("1"), FirstTorchTheme.TEXT));
        graphics.pose().popMatrix();
    }

    private static void label(GuiGraphicsExtractor graphics, String key, int x, int y) {
        graphics.textRenderer().accept(x, y, color(Component.translatable(key), FirstTorchTheme.GOLD));
    }

    private static void item(GuiGraphicsExtractor graphics, String id, int x, int y, int size) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(size / 16F, size / 16F);
        graphics.item(new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(id))), 0, 0);
        graphics.pose().popMatrix();
    }

    private static Component color(Component component, int color) {
        return component.copy().withStyle(style -> style.withColor(color));
    }

    private static void wrapped(GuiGraphicsExtractor graphics, Font font, Component component,
            int x, int y, int width, int maxRows, int color) {
        var lines = font.split(color(component, color), Math.max(1, width));
        for (int row = 0; row < Math.min(maxRows, lines.size()); row++) {
            graphics.textRenderer().accept(x, y + row * 10, lines.get(row));
        }
    }
}
