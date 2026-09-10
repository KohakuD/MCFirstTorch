package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.model.RewardDefinition;
import ch.minenox.firsttorch.network.ProgressPayload;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Scrollable presentation of definitions and server-reported state; never evaluates inventory locally. */
final class LiveDetailsRenderer {
    private LiveDetailsRenderer() {}

    static int draw(GuiGraphicsExtractor graphics, Font font, Rect panel, QuestDefinition quest,
            ProgressPayload progress, int scroll) {
        if (quest == null) return 0;
        int x = panel.x() + 10, w = Math.max(1, panel.width() - 20);
        int top = panel.y() + 10, bottom = panel.bottom() - 34;
        int y = top - scroll;
        boolean available = progress != null && progress.available();
        boolean unlocked = available && quest.prerequisitesMet(progress.state().completedQuestIds());
        graphics.enableScissor(x, top, x + w, bottom);
        Component title = Component.translatable(quest.titleKey()).withStyle(s -> s.withBold(true));
        if (w >= 150) {
            FirstTorchTheme.medallion(graphics, new Rect(x + 1, y + 2, 44, 44), true, false);
            ItemStack badge = QuestIcons.resolve(quest);
            graphics.pose().pushMatrix();
            graphics.pose().translate(x + 9, y + 10);
            graphics.pose().scale(1.75F, 1.75F);
            graphics.item(badge, 0, 0);
            graphics.pose().popMatrix();
            y = Math.max(y + 55, text(graphics, font, title, x + 55, y + 8, w - 55, FirstTorchTheme.TEXT) + 10);
        } else {
            y = text(graphics, font, title, x, y, w, FirstTorchTheme.TEXT) + 8;
        }
        y = text(graphics, font, Component.translatable(quest.descriptionKey()), x, y, w, FirstTorchTheme.MUTED) + 12;
        if (!QuestReferenceLinks.forQuest(quest.id()).isEmpty()) {
            y = text(graphics, font, Component.translatable("screen.firsttorch.reference.more"), x, y, w, FirstTorchTheme.GOLD) + 4;
            y += QuestReferenceLinks.forQuest(quest.id()).size() * 24;
        }
        if (quest.image() != null) {
            var illustration = quest.image();
            var resource = Identifier.parse(illustration.resource());
            var liveRecipe = LiveRecipeCatalog.find(illustration.resource());
            var liveBrewing = LiveBrewingCatalog.find(illustration.resource());
            var livePair = LiveRecipePanels.find(illustration.resource());
            var liveSmelting = LiveSmeltingCatalog.find(illustration.resource());
            if (LiveBlockComparison.supports(illustration.resource())) {
                var bounds = LiveBlockComparison.bounds(x, y, w);
                LiveBlockComparisonRenderer.draw(graphics, bounds);
                y = bounds.bottom() + 5;
            } else if (LiveHandCraftingLayout.supports(illustration.resource())) {
                var bounds = LiveHandCraftingLayout.bounds(x, y, w);
                LiveHandCraftingRenderer.draw(graphics, bounds);
                y = bounds.bottom() + 5;
            } else if (liveRecipe != null) {
                var bounds = GuideImageLayout.bounds(x, y, w, illustration.width(), illustration.height());
                LiveRecipeRenderer.draw(graphics, bounds, liveRecipe);
                y = bounds.bottom() + 5;
            } else if (livePair != null) {
                var bounds = GuideImageLayout.panelBounds(x, y, w, livePair.size());
                LiveRecipeRenderer.drawPanels(graphics, bounds, livePair);
                y = bounds.bottom() + 5;
            } else if (liveSmelting != null) {
                var bounds = LiveSmeltingCatalog.bounds(x, y, w);
                LiveSmeltingRenderer.draw(graphics, bounds, liveSmelting);
                y = bounds.bottom() + 5;
            } else if (liveBrewing != null) {
                var bounds = GuideImageLayout.bounds(x, y, w, illustration.width(), illustration.height());
                LiveBrewingRenderer.draw(graphics, bounds, liveBrewing);
                y = bounds.bottom() + 5;
            } else if (net.minecraft.client.Minecraft.getInstance().getResourceManager().getResource(resource).isPresent()) {
                var bounds = GuideImageLayout.bounds(x, y, w, illustration.width(), illustration.height());
                graphics.blit(resource, bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), 0F, 1F, 0F, 1F);
                y = bounds.bottom() + 5;
            } else {
                y = text(graphics, font, Component.translatable("screen.firsttorch.image.missing"),
                        x, y, w, FirstTorchTheme.MUTED) + 4;
            }
            y = text(graphics, font, Component.translatable(illustration.altKey()), x, y, w, FirstTorchTheme.MUTED) + 12;
        }
        if (!available) {
            y = text(graphics, font, Component.translatable("screen.firsttorch.progress.unavailable"), x, y, w, FirstTorchTheme.GOLD) + 12;
        } else if (!unlocked) {
            y = text(graphics, font, Component.translatable(quest.prerequisiteMode() == QuestDefinition.PrerequisiteMode.ANY
                    ? "screen.firsttorch.prerequisites.any" : "screen.firsttorch.task.locked"), x, y, w, FirstTorchTheme.GOLD) + 12;
            for (var guide : ClientGuideCache.snapshot().guides()) {
                for (var chapter : guide.chapters()) {
                    if (chapter.quests().stream().anyMatch(candidate -> candidate.id().equals(quest.id()))) continue;
                    for (var prerequisite : chapter.quests()) {
                        if (quest.prerequisiteQuestIds().contains(prerequisite.id())
                                && !progress.state().completedQuestIds().contains(prerequisite.id())) {
                            y = text(graphics, font, Component.translatable("screen.firsttorch.prerequisite.external",
                                    Component.translatable(chapter.titleKey()), Component.translatable(prerequisite.titleKey())),
                                    x, y, w, FirstTorchTheme.MUTED) + 8;
                        }
                    }
                }
            }
        }
        y = text(graphics, font, Component.translatable("screen.firsttorch.preview.task"), x, y, w, FirstTorchTheme.GOLD) + 6;
        if (quest.tasks().isEmpty()) {
            y = text(graphics, font, Component.translatable("screen.firsttorch.task.none"), x, y, w, FirstTorchTheme.MUTED) + 10;
        }
        var cards = TaskGridLayout.cards(x, y, w, quest.tasks().size(), (index, width) ->
                font.split(taskLabel(quest.tasks().get(index)), Math.max(1, width - 34)).size() * 10 + 24);
        for (int taskIndex = 0; taskIndex < quest.tasks().size(); taskIndex++) {
            TaskDefinition task = quest.tasks().get(taskIndex);
            Rect card = cards.get(taskIndex);
            int cardX = card.x(), cardY = card.y(), cardWidth = card.width(), cardHeight = card.height();
            boolean done = available && progress.state().completedTaskIds().contains(task.id());
            int count = available ? progress.taskCounts().getOrDefault(task.id(), 0) : 0;
            if (done) count = task.count();
            ItemStack item = QuestIcons.resolveTask(task, quest);
            Component label = taskLabel(task);
            FirstTorchTheme.inset(graphics, card);
            if (done) FirstTorchTheme.completionBadge(graphics, cardX + 7, cardY + 8);
            else graphics.item(item, cardX + 7, cardY + 8);
            text(graphics, font, label, cardX + 30, cardY + 6, Math.max(1, cardWidth - 34), FirstTorchTheme.TEXT);
            Component amount = available ? Component.literal(count + " / " + task.count()) : Component.literal("— / " + task.count());
            text(graphics, font, amount, cardX + 30, cardY + cardHeight - 17, Math.max(1, cardWidth - 34), FirstTorchTheme.MUTED);
            graphics.fill(cardX + 4, cardY + cardHeight - 5, cardX + cardWidth - 4, cardY + cardHeight - 3, 0xFF080A0B);
            if (available) graphics.fill(cardX + 4, cardY + cardHeight - 5,
                    cardX + 4 + Math.max(0, cardWidth - 8) * Math.min(count, task.count()) / task.count(),
                    cardY + cardHeight - 3, FirstTorchTheme.GOLD);
            y = Math.max(y, card.bottom() + TaskGridLayout.GAP);
        }
        if (!quest.rewards().isEmpty()) {
            y += 6;
            String rewardKey = available && progress.pendingQuestIds().contains(quest.id()) ? "screen.firsttorch.reward.recovery"
                    : available && progress.claimedQuestIds().contains(quest.id()) ? "screen.firsttorch.reward.claimed"
                    : "screen.firsttorch.reward.title";
            y = text(graphics, font, Component.translatable(rewardKey), x, y, w, FirstTorchTheme.GOLD) + 6;
            for (RewardDefinition reward : quest.rewards()) {
                ItemStack icon = reward.itemId() == null ? new ItemStack(Items.EXPERIENCE_BOTTLE) : item(reward.itemId());
                graphics.item(icon, x + 4, y);
                Component label = reward.type() == RewardDefinition.Type.EXPERIENCE
                        ? Component.translatable("screen.firsttorch.preview.reward.xp", reward.amount())
                        : Component.literal(reward.amount() + " × ").append(icon.getHoverName());
                y = Math.max(y + 22, text(graphics, font, label, x + 26, y + 4, Math.max(1, w - 26), FirstTorchTheme.MUTED) + 6);
            }
        }
        graphics.disableScissor();
        return Math.max(0, y + scroll - bottom + 4);
    }

    private static Component taskLabel(TaskDefinition task) {
        if (task.titleKey() != null) return Component.translatable(task.titleKey());
        if (task.type() == TaskDefinition.Type.MANUAL) return Component.translatable("screen.firsttorch.task.manual");
        if (task.type() == TaskDefinition.Type.ADVANCEMENT) return Component.literal(task.advancementId()
                + (task.criterion() == null ? "" : " / " + task.criterion()));
        if (task.type() == TaskDefinition.Type.INVENTORY_TAG) return Component.translatableWithFallback(
                "task.firsttorch.tag." + task.itemId().replace(':', '.').replace('/', '.'), "#" + task.itemId());
        return item(task.itemId()).getHoverName();
    }

    /** Shared text metrics keep clickable controls aligned with the scrolled content. */
    static int referenceTop(Font font, Rect panel, QuestDefinition quest) {
        int w = Math.max(1, panel.width() - 20);
        int y = panel.y() + 10;
        Component title = Component.translatable(quest.titleKey()).withStyle(s -> s.withBold(true));
        y += w >= 150 ? Math.max(55, 18 + font.split(title, w - 55).size() * 10)
                : font.split(title, w).size() * 10 + 8;
        y += font.split(Component.translatable(quest.descriptionKey()), w).size() * 10 + 12;
        return y + font.split(Component.translatable("screen.firsttorch.reference.more"), w).size() * 10 + 4;
    }

    private static ItemStack item(String id) {
        return new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(id)));
    }

    private static int text(GuiGraphicsExtractor graphics, Font font, Component label, int x, int y, int width, int color) {
        var lines = font.split(label.copy().withStyle(s -> s.withColor(color)), Math.max(1, width));
        for (var line : lines) {
            graphics.textRenderer().accept(x, y, line);
            y += 10;
        }
        return y;
    }
}
