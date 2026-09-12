package ch.minenox.firsttorch.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Bridges normalized installed-texture regions to the 1.21.1 GUI API. */
final class LiveRenderCompat {
    private LiveRenderCompat() {}

    static void text(GuiGraphics graphics, Component text, int left, int right, int top, int bottom) {
        var font = Minecraft.getInstance().font;
        int width = font.width(text);
        int y = (top + bottom - font.lineHeight) / 2 + 1;
        int available = right - left;
        if (width <= available) {
            graphics.drawCenteredString(font, text, (left + right) / 2, y, 0xFFFFFFFF);
            return;
        }
        int overflow = width - available;
        double time = net.minecraft.Util.getMillis() / 1000.0;
        double duration = Math.max(overflow * .5, 3.0);
        double progress = Math.sin(Math.PI / 2 * Math.cos(Math.PI * 2 * time / duration)) / 2 + .5;
        FirstTorchGui.enableScissor(graphics, left, top, right, bottom);
        try {
            graphics.drawString(font, text, left - (int) (overflow * progress), y, 0xFFFFFFFF);
        } finally {
            graphics.disableScissor();
        }
    }

    static void blit(GuiGraphics graphics, ResourceLocation texture, int left, int top, int right, int bottom,
            float u0, float u1, float v0, float v1) {
        // The shared scene uses the modern location; 1.21.1 ships the same vanilla
        // portal surface under this older resource path.
        if (texture.equals(ResourceLocation.parse("minecraft:textures/entity/end_portal/end_portal.png"))) {
            texture = ResourceLocation.parse("minecraft:textures/entity/end_portal.png");
        }
        // Unlike 26.x extraction, 1.21.1 blits draw immediately. Finish queued
        // frame/text geometry first and explicitly blend transparent game textures.
        graphics.flush();
        boolean blending = org.lwjgl.opengl.GL11.glIsEnabled(org.lwjgl.opengl.GL11.GL_BLEND);
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        try {
            graphics.blit(texture, left, top, right - left, bottom - top,
                    u0 * 4096F, v0 * 4096F, Math.round((u1 - u0) * 4096F), Math.round((v1 - v0) * 4096F), 4096, 4096);
        } finally {
            if (!blending) com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        }
    }
}
