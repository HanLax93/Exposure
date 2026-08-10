package io.github.mortuusars.exposure.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.world.entity.CameraStandEntity;
import io.github.mortuusars.exposure.world.item.camera.CameraItem;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CameraStandEntityRenderer
        extends EntityRenderer<CameraStandEntity, CameraStandRenderState> {
    public static final float MOUNT_SCALE = 0.9f;

    private final ItemModelResolver itemModelResolver;

    public CameraStandEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public CameraStandRenderState createRenderState() {
        return new CameraStandRenderState();
    }

    @Override
    public void extractRenderState(CameraStandEntity entity, CameraStandRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        state.hurtTime = entity.getHurtTime() - partialTick;
        state.damage = Math.max(0, entity.getDamage() - partialTick);
        state.hurtDirection = entity.getHurtDir();
        state.malfunctioned = entity.isMalfunctioned();
        state.vehicleYRot = entity.getVehicle() == null
                ? null
                : Mth.rotLerp(partialTick, entity.getVehicle().yRotO, entity.getVehicle().getYRot());

        ItemStack stand = new ItemStack(Exposure.Items.CAMERA_STAND.get());
        itemModelResolver.updateForNonLiving(state.stand, stand, ItemDisplayContext.FIXED, entity);
        itemModelResolver.updateForNonLiving(state.mount, stand, ItemDisplayContext.NONE, entity);

        ItemStack camera = entity.getCamera();
        state.cameraScale = camera.getItem() instanceof CameraItem cameraItem
                ? cameraItem.getScaleOnStand()
                : MOUNT_SCALE;
        itemModelResolver.updateForNonLiving(state.camera, camera, ItemDisplayContext.NONE, entity);
    }

    @Override
    public void submit(CameraStandRenderState state, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        super.submit(state, poseStack, collector, cameraRenderState);

        poseStack.pushPose();
        if (state.hurtTime > 0.0F) {
            float rotation = Mth.sin(state.hurtTime) * state.hurtTime * state.damage / 10.0F * state.hurtDirection;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            poseStack.mulPose(Axis.XP.rotationDegrees(rotation));
        }

        poseStack.pushPose();
        if (state.vehicleYRot != null) {
            poseStack.mulPose(Axis.YP.rotationDegrees(-state.vehicleYRot + 45));
        }
        poseStack.translate(0, 0.5, 0);
        state.stand.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 1.125, 0);
        poseStack.scale(MOUNT_SCALE, MOUNT_SCALE, MOUNT_SCALE);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.yRot + 180));
        poseStack.mulPose(Axis.XP.rotationDegrees(-state.xRot));
        if (state.malfunctioned) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-50));
            poseStack.mulPose(Axis.XP.rotationDegrees(-10));
        }
        poseStack.translate(0, 0.5, 0);
        state.mount.submit(poseStack, collector, state.lightCoords,
                OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();

        if (!state.camera.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0, 1.125, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(-state.yRot + 180));
            poseStack.mulPose(Axis.XP.rotationDegrees(-state.xRot));
            poseStack.translate(0, 0.125 * MOUNT_SCALE, 0);
            if (state.malfunctioned) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(-50));
                poseStack.mulPose(Axis.XP.rotationDegrees(-15));
            }
            poseStack.scale(state.cameraScale, state.cameraScale, state.cameraScale);
            poseStack.translate(0, 0.5, 0);
            state.camera.submit(poseStack, collector, state.lightCoords,
                    OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
