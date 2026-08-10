package io.github.mortuusars.exposure.client.util;

import io.github.mortuusars.exposure.util.Rect2f;
import net.minecraft.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class GuiUtil {
    /**
     * Packs the old shader-color values into the 8-bit tint used by 26.2 GUI
     * rendering. Values above 1 are normalized together instead of individually
     * clamped, preserving their channel ratios (for example 1.2/.96/.75 becomes
     * 1/.8/.625 rather than 1/.96/.75).
     */
    public static int normalizedTint(float red, float green, float blue, float alpha) {
        float scale = Math.max(1.0F, Math.max(red, Math.max(green, blue)));
        return ARGB.colorFromFloat(
                Mth.clamp(alpha, 0.0F, 1.0F),
                Mth.clamp(red / scale, 0.0F, 1.0F),
                Mth.clamp(green / scale, 0.0F, 1.0F),
                Mth.clamp(blue / scale, 0.0F, 1.0F));
    }

    public static void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y,
                            int u, int v, int width, int height) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, 256, 256);
    }

    public static void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y,
                            int u, int v, int width, int height, int textureWidth, int textureHeight) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public static void blitColored(GuiGraphicsExtractor graphics, Identifier texture, int x, int y,
                                   int u, int v, int width, int height, int textureWidth, int textureHeight, int color) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height,
                textureWidth, textureHeight, color);
    }

    public static void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int ignoredZ,
                            int u, int v, int width, int height, int textureWidth, int textureHeight) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawRect(GuiGraphicsExtractor guiGraphics, Rect2f rect, int color) {
        drawRect(guiGraphics, rect.x, rect.y, rect.width, rect.height, color);
    }

    public static void drawRect(GuiGraphicsExtractor guiGraphics, float x, float y, float width, float height, int color) {
        guiGraphics.fill(Mth.floor(x), Mth.floor(y), Mth.ceil(x + width), Mth.ceil(y + height), color);
    }

    // --

    public static void renderScrollingString(GuiGraphicsExtractor guiGraphics, Font font, Component text, int x, int y, int width, int color) {
        renderScrollingString(guiGraphics, font, text, x, y, x + width, y + font.lineHeight, color);
    }

    public static void renderScrollingString(GuiGraphicsExtractor guiGraphics, Font font, Component text, int minX, int minY, int maxX, int maxY, int color) {
        renderScrollingString(guiGraphics, font, text, (minX + maxX) / 2, minX, minY, maxX, maxY, color);
    }

    // Doesn't work in toast for some reason.
    public static void renderScrollingString(GuiGraphicsExtractor guiGraphics, Font font, Component text, int centerX, int minX, int minY, int maxX, int maxY, int color) {
        int fontWidth = font.width(text);
        int y = (minY + maxY - 9) / 2 + 1;
        int width = maxX - minX;
        if (fontWidth > width) {
            int remaining = fontWidth - width;
            double d = (double) Util.getMillis() / 400;
            double e = Math.max((double)remaining * 0.5, 3.0);
            double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, remaining);
            guiGraphics.enableScissor(minX, minY, maxX, maxY);
            guiGraphics.text(font, text, minX - (int)g, y, color, false);
            guiGraphics.disableScissor();
        } else {
            int l = Mth.clamp(centerX, minX + fontWidth / 2, maxX - fontWidth / 2);
            guiGraphics.centeredText(font, text, l, y, color);
        }
    }
}
