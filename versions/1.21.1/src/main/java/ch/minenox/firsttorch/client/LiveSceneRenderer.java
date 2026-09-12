package ch.minenox.firsttorch.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Renders explanatory scenes without loading copied game artwork or spawning world entities. */
final class LiveSceneRenderer {
    private static final Map<String, Entity> MODELS = new HashMap<>();
    private static ClientLevel modelLevel;
    private static final net.minecraft.world.scores.PlayerTeam GUIDE_TEAM = guideTeam();

    private static net.minecraft.world.scores.PlayerTeam guideTeam() {
        var team = new net.minecraft.world.scores.PlayerTeam(new net.minecraft.world.scores.Scoreboard(), "firsttorch_guide");
        team.setNameTagVisibility(net.minecraft.world.scores.Team.Visibility.NEVER);
        return team;
    }

    private LiveSceneRenderer() {}
    static void clear() { MODELS.clear(); modelLevel = null; LiveBlockTopRenderer.clear(); }

    static void draw(GuiGraphics graphics, FirstTorchLayout.Rect bounds, LiveScene scene) {
        graphics.pose().pushPose();
        graphics.pose().translate(bounds.x(), bounds.y(), 0);
        graphics.pose().scale(bounds.width() / (float) scene.width(), bounds.height() / (float) scene.height(), 1F);
        FirstTorchTheme.frame(graphics, new FirstTorchLayout.Rect(0, 0, scene.width(), scene.height()), false);
        for (var operation : scene.operations()) {
            if (operation instanceof LiveScene.Item item) {
                graphics.pose().pushPose();
                graphics.pose().translate(item.x(), item.y(), 0);
                graphics.pose().scale(item.size() / 16F, item.size() / 16F, 1F);
                graphics.renderItem(QuestIcons.resolveItem(item.id()), 0, 0);
                graphics.pose().popPose();
            } else if (operation instanceof LiveScene.Texture texture) {
                LiveRenderCompat.blit(graphics, ResourceLocation.parse(texture.path()), texture.x(), texture.y(), texture.x() + texture.width(),
                        texture.y() + texture.height(), texture.u0(), texture.u1(), texture.v0(), texture.v1());
            } else if (operation instanceof LiveScene.TintedTexture tinted) {
                tinted(graphics, tinted.texture(), tinted.color());
            } else if (operation instanceof LiveScene.Wire wire) {
                // Installed east-west dust texture with Minecraft's actual per-power colour.
                graphics.pose().pushPose();
                graphics.pose().translate(wire.x() + wire.size() / 2F, wire.y() + wire.size() / 2F, 0);
                graphics.pose().mulPose(new org.joml.Quaternionf().rotateZ(-(float) Math.PI / 2));
                tinted(graphics, new LiveScene.Texture("minecraft:textures/block/redstone_dust_line1.png",
                        -wire.size() / 2, -wire.size() / 2, wire.size(), wire.size(), 0, 0, 1, 1),
                        net.minecraft.world.level.block.RedStoneWireBlock.getColorForPower(wire.power()));
                LiveRenderCompat.blit(graphics, ResourceLocation.parse("minecraft:textures/block/redstone_dust_overlay.png"),
                        -wire.size() / 2, -wire.size() / 2, wire.size() - wire.size() / 2, wire.size() - wire.size() / 2, 0, 1, 0, 1);
                graphics.pose().popPose();
            } else if (operation instanceof LiveScene.Box box) {
                if (box.outline()) graphics.renderOutline(box.x(), box.y(), box.width(), box.height(), box.color());
                else graphics.fill(box.x(), box.y(), box.x() + box.width(), box.y() + box.height(), box.color());
            } else if (operation instanceof LiveScene.Line line) {
                stroke(graphics, line.x1(), line.y1(), line.x2(), line.y2(), line.thickness(), line.color());
                if (line.arrow()) {
                    double angle = Math.atan2(line.y2() - line.y1(), line.x2() - line.x1());
                    int size = Math.max(7, line.thickness() * 3);
                    for (double turn : new double[]{-.55, .55}) {
                        stroke(graphics, line.x2(), line.y2(),
                                line.x2() - (int) Math.round(Math.cos(angle + turn) * size),
                                line.y2() - (int) Math.round(Math.sin(angle + turn) * size), line.thickness(), line.color());
                    }
                }
            } else if (operation instanceof LiveScene.Text text) {
                graphics.pose().pushPose();
                graphics.pose().translate(text.x(), text.y(), 0);
                float textScale = Math.max(1F, text.height() / 14F);
                graphics.pose().scale(textScale, textScale, 1F);
                LiveRenderCompat.text(graphics, text.translated()
                        ? Component.translatable(text.value()) : Component.literal(text.value()),
                        0, Math.max(1, Math.round(text.width() / textScale)), 0, Math.round(text.height() / textScale));
                graphics.pose().popPose();
            } else if (operation instanceof LiveScene.Entity entity) {
                entity(graphics, entity);
            } else if (operation instanceof LiveScene.BlockTop block) {
                LiveBlockTopRenderer.draw(graphics, ResourceLocation.parse(block.model()), block.quarterTurns(), block.x(), block.y(), block.size());
            }
        }
        graphics.pose().popPose();
    }

    private static void tinted(GuiGraphics graphics, LiveScene.Texture texture, int color) {
        graphics.flush();
        float[] previousColor = com.mojang.blaze3d.systems.RenderSystem.getShaderColor().clone();
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(((color >> 16) & 255) / 255F,
                ((color >> 8) & 255) / 255F, (color & 255) / 255F, 1F);
        try {
            LiveRenderCompat.blit(graphics, ResourceLocation.parse(texture.path()), texture.x(), texture.y(),
                    texture.x() + texture.width(), texture.y() + texture.height(),
                    texture.u0(), texture.u1(), texture.v0(), texture.v1());
            graphics.flush();
        } finally {
            com.mojang.blaze3d.systems.RenderSystem.setShaderColor(previousColor[0], previousColor[1], previousColor[2], previousColor[3]);
        }
    }

    private static void stroke(GuiGraphics graphics, int x1, int y1, int x2, int y2, int thickness, int color) {
        int steps = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
        int radius = thickness / 2;
        for (int step = 0; step <= steps; step++) {
            float ratio = steps == 0 ? 0 : step / (float) steps;
            int x = Math.round(x1 + (x2 - x1) * ratio), y = Math.round(y1 + (y2 - y1) * ratio);
            graphics.fill(x - radius, y - radius, x - radius + thickness, y - radius + thickness, color);
        }
    }

    private static void entity(GuiGraphics graphics, LiveScene.Entity op) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        if (minecraft.level != modelLevel) { clear(); modelLevel = minecraft.level; }
        var entity = MODELS.computeIfAbsent(op.id(), id -> {
            if (id.equals("minecraft:player")) {
                return new net.minecraft.client.player.RemotePlayer(minecraft.level,
                        new com.mojang.authlib.GameProfile(new java.util.UUID(0, 0), "FirstTorchGuide")) {
                    @Override
                    public net.minecraft.world.scores.PlayerTeam getTeam() { return GUIDE_TEAM; }
                    @Override
                    public net.minecraft.client.resources.PlayerSkin getSkin() {
                        return new net.minecraft.client.resources.PlayerSkin(
                                ResourceLocation.parse("minecraft:textures/entity/player/wide/steve.png"),
                                null, null, null, net.minecraft.client.resources.PlayerSkin.Model.WIDE, true);
                    }
                };
            }
            var parts = id.split("#", 2);
            var type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(parts[0]));
            var model = type == null ? null : type.create(minecraft.level);
            if (parts.length == 2 && parts[1].equals("baby") && model instanceof net.minecraft.world.entity.monster.piglin.Piglin piglin) piglin.setBaby(true);
            return model;
        });
        if (!(entity instanceof LivingEntity living)) {
            throw new IllegalArgumentException("Unsupported First Torch scene entity: " + op.id());
        }
        living.yBodyRot = 160F;
        living.setYRot(160F);
        living.yHeadRot = 160F;
        living.yHeadRotO = 160F;
        living.setXRot(0F);
        float scale = Math.min(op.width() / Math.max(.5F, living.getBbWidth() * 1.5F),
                op.height() / Math.max(.5F, living.getBbHeight() * 1.15F));
        // The 1.21.1 inventory renderer preserves the current GUI PoseStack, including
        // the scene's scale/scroll transform, and uses installed entity models/skins.
        graphics.flush();
        var dispatcher = minecraft.getEntityRenderDispatcher();
        var cameraOrientation = new Quaternionf(dispatcher.cameraOrientation());
        boolean hitBoxes = dispatcher.shouldRenderHitBoxes();
        FirstTorchGui.enableScissor(graphics, op.x(), op.y(), op.x() + op.width(), op.y() + op.height());
        dispatcher.setRenderHitBoxes(false);
        try {
            net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory(graphics,
                    op.x() + op.width() / 2F, op.y() + op.height() / 2F, scale,
                    new Vector3f(0, living.getBbHeight() / 2F, 0),
                    new Quaternionf().rotateZ((float) Math.PI), new Quaternionf(), living);
        } finally {
            dispatcher.overrideCameraOrientation(cameraOrientation);
            dispatcher.setRenderHitBoxes(hitBoxes);
            graphics.disableScissor();
        }
    }
}
