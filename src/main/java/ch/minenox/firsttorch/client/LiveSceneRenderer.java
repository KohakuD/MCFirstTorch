package ch.minenox.firsttorch.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

/** Renders explanatory scenes without loading copied game artwork or spawning world entities. */
final class LiveSceneRenderer {
    private static final Map<String, Entity> MODELS = new HashMap<>();
    private static ClientLevel modelLevel;
    private static net.minecraft.client.model.player.PlayerModel playerModel;
    private LiveSceneRenderer() {}
    static void clear() { MODELS.clear(); modelLevel = null; playerModel = null; LiveBlockTopRenderer.clear(); }

    static void draw(GuiGraphicsExtractor graphics, FirstTorchLayout.Rect bounds, LiveScene scene) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(bounds.x(), bounds.y());
        graphics.pose().scale(bounds.width() / (float) scene.width(), bounds.height() / (float) scene.height());
        FirstTorchTheme.frame(graphics, new FirstTorchLayout.Rect(0, 0, scene.width(), scene.height()), false);
        for (var operation : scene.operations()) {
            if (operation instanceof LiveScene.Item item) {
                graphics.pose().pushMatrix();
                graphics.pose().translate(item.x(), item.y());
                graphics.pose().scale(item.size() / 16F, item.size() / 16F);
                graphics.item(QuestIcons.resolveItem(item.id()), 0, 0);
                graphics.pose().popMatrix();
            } else if (operation instanceof LiveScene.Texture texture) {
                graphics.blit(Identifier.parse(texture.path()), texture.x(), texture.y(), texture.x() + texture.width(),
                        texture.y() + texture.height(), texture.u0(), texture.u1(), texture.v0(), texture.v1());
            } else if (operation instanceof LiveScene.TintedTexture tinted) {
                tinted(graphics, tinted.texture(), tinted.color());
            } else if (operation instanceof LiveScene.Wire wire) {
                // Installed east-west dust texture with Minecraft's actual per-power colour.
                graphics.pose().pushMatrix();
                graphics.pose().translate(wire.x() + wire.size() / 2F, wire.y() + wire.size() / 2F);
                graphics.pose().rotate(-(float) Math.PI / 2);
                tinted(graphics, new LiveScene.Texture("minecraft:textures/block/redstone_dust_line1.png",
                        -wire.size() / 2, -wire.size() / 2, wire.size(), wire.size(), 0, 0, 1, 1),
                        net.minecraft.world.level.block.RedStoneWireBlock.getColorForPower(wire.power()));
                graphics.blit(Identifier.parse("minecraft:textures/block/redstone_dust_overlay.png"),
                        -wire.size() / 2, -wire.size() / 2, wire.size() - wire.size() / 2, wire.size() - wire.size() / 2, 0, 1, 0, 1);
                graphics.pose().popMatrix();
            } else if (operation instanceof LiveScene.Box box) {
                if (box.outline()) graphics.outline(box.x(), box.y(), box.width(), box.height(), box.color());
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
                graphics.pose().pushMatrix();
                graphics.pose().translate(text.x(), text.y());
                float textScale = Math.max(1F, text.height() / 14F);
                graphics.pose().scale(textScale, textScale);
                graphics.textRenderer().acceptScrollingWithDefaultCenter(text.translated()
                        ? Component.translatable(text.value()) : Component.literal(text.value()),
                        0, Math.max(1, Math.round(text.width() / textScale)), 0, Math.round(text.height() / textScale));
                graphics.pose().popMatrix();
            } else if (operation instanceof LiveScene.Entity entity) {
                entity(graphics, entity);
            } else if (operation instanceof LiveScene.BlockTop block) {
                LiveBlockTopRenderer.draw(graphics, Identifier.parse(block.model()), block.quarterTurns(), block.x(), block.y(), block.size());
            }
        }
        graphics.pose().popMatrix();
    }

    private static void tinted(GuiGraphicsExtractor graphics, LiveScene.Texture texture, int color) {
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, Identifier.parse(texture.path()),
                texture.x(), texture.y(), texture.u0() * 4096F, texture.v0() * 4096F, texture.width(), texture.height(),
                Math.round((texture.u1() - texture.u0()) * 4096), Math.round((texture.v1() - texture.v0()) * 4096),
                4096, 4096, color);
    }

    private static void stroke(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int thickness, int color) {
        int steps = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
        int radius = thickness / 2;
        for (int step = 0; step <= steps; step++) {
            float ratio = steps == 0 ? 0 : step / (float) steps;
            int x = Math.round(x1 + (x2 - x1) * ratio), y = Math.round(y1 + (y2 - y1) * ratio);
            graphics.fill(x - radius, y - radius, x - radius + thickness, y - radius + thickness, color);
        }
    }

    private static void entity(GuiGraphicsExtractor graphics, LiveScene.Entity op) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        if (minecraft.level != modelLevel) { clear(); modelLevel = minecraft.level; }
        var a = graphics.pose().transformPosition(op.x(), op.y(), new Vector2f());
        var b = graphics.pose().transformPosition(op.x() + op.width(), op.y() + op.height(), new Vector2f());
        if (op.id().equals("minecraft:player")) {
            if (playerModel == null) playerModel = new net.minecraft.client.model.player.PlayerModel(
                    minecraft.getEntityModels().bakeLayer(net.minecraft.client.model.geom.ModelLayers.PLAYER), false);
            graphics.skin(playerModel, Identifier.parse("minecraft:textures/entity/player/wide/steve.png"),
                    Math.min((b.y - a.y) * .97F / 2.125F, (b.x - a.x) / .9F), -5F, 25F, -1.0625F,
                    Math.round(a.x), Math.round(a.y), Math.round(b.x), Math.round(b.y));
            return;
        }
        var entity = MODELS.computeIfAbsent(op.id(), id -> {
            var parts = id.split("#", 2);
            var type = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(parts[0]));
            var model = type == null ? null : type.create(minecraft.level, EntitySpawnReason.LOAD);
            if (parts.length == 2 && parts[1].equals("baby") && model instanceof net.minecraft.world.entity.monster.piglin.Piglin piglin) piglin.setBaby(true);
            return model;
        });
        if (entity == null) return;
        var renderer = minecraft.getEntityRenderDispatcher().getRenderer(entity);
        var state = renderer.createRenderState(entity, 0F);
        state.shadowPieces.clear();
        state.outlineColor = 0;
        state.nameTag = null;
        if (state instanceof LivingEntityRenderState living) {
            living.bodyRot = 160F;
            living.yRot = 0;
            living.xRot = 0;
        }
        // Picture-in-picture ignores the normal GUI pose. Apply it explicitly so enlarged
        // browser views and scrolled scenes use exactly the same physical coordinates.
        float scale = Math.min((b.x - a.x) / Math.max(.5F, state.boundingBoxWidth * 1.5F),
                (b.y - a.y) / Math.max(.5F, state.boundingBoxHeight * 1.15F));
        graphics.entity(state, scale, new Vector3f(0, state.boundingBoxHeight / 2, 0),
                new Quaternionf().rotateZ((float) Math.PI), null,
                Math.round(a.x), Math.round(a.y), Math.round(b.x), Math.round(b.y));
    }
}
