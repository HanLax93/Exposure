package io.github.mortuusars.exposure.client.render.image;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.client.image.renderable.RenderableImage;
import io.github.mortuusars.exposure.util.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

/**
 * Credits to <a href="https://github.com/Jalvaviel/MapMipMapMod">MapMipMapMod by Jalvaviel</a> for example of mipmap implementation for dynamic images.
 * And to <a href="https://github.com/bravely-beep">bravely-beep</a> for pointing me to it.
 */
public class RenderedImageInstance implements AutoCloseable {
    protected final Identifier textureLocation;
    protected RenderableImage image;
    protected DynamicTexture texture;
    protected final RenderType renderType;
    protected boolean requiresUpload = true;

    RenderedImageInstance(RenderableImage image) {
        this.image = image;
        this.texture = new DynamicTexture("Exposure image " + image.getIdentifier(), image.width(), image.height(), true);
        this.textureLocation = image.getIdentifier().toResourceLocation();
        Minecraft.getInstance().getTextureManager().register(textureLocation, this.texture);

        renderType = RenderTypes.text(textureLocation);

        forceUpload();
    }

    public void replaceData(RenderableImage image) {
        boolean hasChanged = !image.getIdentifier().equals(this.image.getIdentifier());
        this.image = image;
        if (hasChanged) {
            this.texture.close();
            this.texture = new DynamicTexture("Exposure image " + image.getIdentifier(), image.width(), image.height(), true);
            Minecraft.getInstance().getTextureManager().register(textureLocation, this.texture);
            forceUpload();
        }
    }

    public void forceUpload() {
        this.requiresUpload = true;
    }

    protected void updateTexture() {
        if (texture.getPixels() == null) return;

        int width = this.image.width();
        int height = this.image.height();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = this.image.getPixelARGB(x, y);
                this.texture.getPixels().setPixelABGR(x, y, Color.ABGRtoARGB(argb));
            }
        }

        this.texture.upload();
    }

    public Identifier prepareTexture() {
        if (this.requiresUpload) {
            this.updateTexture();
            this.requiresUpload = false;
        }
        return textureLocation;
    }

    public void draw(PoseStack poseStack, SubmitNodeCollector collector, float minX, float minY, float maxX, float maxY,
                     float minU, float minV, float maxU, float maxV, int packedLight, int r, int g, int b, int a) {
        if (this.requiresUpload) {
            this.updateTexture();
            this.requiresUpload = false;
        }

        collector.submitCustomGeometry(poseStack, this.renderType, (pose, vertices) -> {
            vertices.addVertex(pose, minX, maxY, 0).setColor(r, g, b, a).setUv(minU, maxV).setLight(packedLight);
            vertices.addVertex(pose, maxX, maxY, 0).setColor(r, g, b, a).setUv(maxU, maxV).setLight(packedLight);
            vertices.addVertex(pose, maxX, minY, 0).setColor(r, g, b, a).setUv(maxU, minV).setLight(packedLight);
            vertices.addVertex(pose, minX, minY, 0).setColor(r, g, b, a).setUv(minU, minV).setLight(packedLight);
        });
    }

    public void close() {
        Minecraft.getInstance().getTextureManager().release(textureLocation);
        this.texture.close();
    }
}
