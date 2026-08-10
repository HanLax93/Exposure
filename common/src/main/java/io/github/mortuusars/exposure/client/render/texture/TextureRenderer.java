package io.github.mortuusars.exposure.client.render.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.exposure.util.color.Color;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class TextureRenderer {
    public static void render(PoseStack poseStack, SubmitNodeCollector collector, Identifier texture,
                              int packedLight, Color color) {
        render(poseStack, collector, texture, packedLight, color.getR(), color.getG(), color.getB(), color.getA());
    }

    public static void render(PoseStack poseStack, SubmitNodeCollector collector, Identifier texture,
                              int packedLight, int r, int g, int b, int a) {
        render(poseStack, collector, texture, 0, 0, 1, 1, packedLight, r, g, b, a);
    }

    public static void render(PoseStack poseStack, SubmitNodeCollector collector, Identifier texture,
                              float x, float y, float width, float height, int packedLight, int r, int g, int b, int a) {
        render(poseStack, collector, texture, x, y, x + width, y + height,
                0, 0, 1, 1, packedLight, r, g, b, a);
    }

    public static void render(PoseStack poseStack, SubmitNodeCollector collector, Identifier texture,
                              float minX, float minY, float maxX, float maxY,
                              float minU, float minV, float maxU, float maxV, int packedLight, int r, int g, int b, int a) {
        collector.submitCustomGeometry(poseStack, RenderTypes.text(texture), (pose, vertices) -> {
            vertices.addVertex(pose, minX, maxY, 0).setColor(r, g, b, a).setUv(minU, maxV).setLight(packedLight);
            vertices.addVertex(pose, maxX, maxY, 0).setColor(r, g, b, a).setUv(maxU, maxV).setLight(packedLight);
            vertices.addVertex(pose, maxX, minY, 0).setColor(r, g, b, a).setUv(maxU, minV).setLight(packedLight);
            vertices.addVertex(pose, minX, minY, 0).setColor(r, g, b, a).setUv(minU, minV).setLight(packedLight);
        });
    }
}
