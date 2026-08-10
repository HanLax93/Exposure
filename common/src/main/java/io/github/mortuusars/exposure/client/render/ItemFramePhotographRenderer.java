package io.github.mortuusars.exposure.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.mortuusars.exposure.ExposureClient;
import io.github.mortuusars.exposure.world.item.PhotographItem;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class ItemFramePhotographRenderer {
    public static void render(ItemFrameRenderState itemFrame, PoseStack poseStack, SubmitNodeCollector bufferSource,
                                 int packedLight, PhotographItem item, ItemStack stack) {
        if (itemFrame.isGlowFrame)
            packedLight = LightCoordsUtil.FULL_BRIGHT;

        poseStack.pushPose();

        String entityName = BuiltInRegistries.ENTITY_TYPE.getKey(itemFrame.entityType).toString();
        if (entityName.equals("quark:glass_frame")) {
            poseStack.translate(0, 0, 0.475f);
        }

        // Snap to 90 degrees like a map.
        poseStack.mulPose(Axis.ZP.rotationDegrees(45 * itemFrame.rotation));

        float pixelSize = 0.0625f;
        float scale = 1f - pixelSize * 6; // 3px from each side

        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5, -0.5, 0.045);

        ExposureClient.photographRenderer().renderPhotograph(poseStack, bufferSource, item, stack,
                false, false, packedLight, 255, 255, 255, 255);

        poseStack.popPose();
    }
}
