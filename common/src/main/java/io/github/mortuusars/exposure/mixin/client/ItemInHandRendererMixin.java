package io.github.mortuusars.exposure.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.mortuusars.exposure.ExposureClient;
import io.github.mortuusars.exposure.client.camera.CameraClient;
import io.github.mortuusars.exposure.world.item.PhotographItem;
import io.github.mortuusars.exposure.world.item.StackedPhotographsItem;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow
    protected abstract void renderPlayerArm(PoseStack poseStack, SubmitNodeCollector buffer, int packedLight,
                                            float equippedProgress, float swingProgress, HumanoidArm arm,
                                            PlayerRenderState playerState);
    @Shadow
    protected abstract float calculateMapTilt(float pPitch);
    @Shadow
    protected abstract void renderMapHand(PoseStack poseStack, SubmitNodeCollector buffer, int packedLight,
                                          HumanoidArm arm, PlayerRenderState playerState);

    @Inject(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 0),
            cancellable = true)
    private void renderPhotograph(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state,
                                  float partialTicks, float pitch, InteractionHand hand,
                                  float swingProgress, ItemStack stack, float equipProgress, PoseStack poseStack,
                                  SubmitNodeCollector buffer, int combinedLight, CallbackInfo ci, @Local boolean isMainHand, @Local HumanoidArm arm) {
        if (CameraClient.viewfinder() != null && CameraClient.viewfinder().isLookingThrough()) {
            poseStack.popPose();
            ci.cancel();
            return;
        }

        if (stack.getItem() instanceof PhotographItem || stack.getItem() instanceof StackedPhotographsItem) {
            if (isMainHand && state.offHandItem.isEmpty()) {
                exposure$renderTwoHandedPhotograph(playerState, state, poseStack, buffer, combinedLight, pitch, equipProgress, swingProgress);
            } else {
                exposure$renderOneHandedPhotograph(playerState, poseStack, buffer, combinedLight, equipProgress, arm, swingProgress, stack);
            }

            poseStack.popPose();

            ci.cancel();
        }
    }

    @Unique
    private void exposure$renderOneHandedPhotograph(PlayerRenderState playerState, PoseStack poseStack, SubmitNodeCollector buffer, int packedLight, float pEquippedProgress, HumanoidArm pHand, float pSwingProgress, ItemStack stack) {
        float f = pHand == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        poseStack.translate(f * 0.125F, -0.125D, 0.0D);
        if (!playerState.avatarRenderState.isInvisible) {
            poseStack.pushPose();
            poseStack.rotateDegrees(Axis.ZP, f * 10.0F);
            this.renderPlayerArm(poseStack, buffer, packedLight, pEquippedProgress, pSwingProgress, pHand, playerState);
            poseStack.popPose();
        }

        poseStack.pushPose();
        poseStack.translate(f * 0.51F, -0.08F + pEquippedProgress * -1.2F, -0.75D);
        float f1 = Mth.sqrt(pSwingProgress);
        float f2 = Mth.sin(f1 * (float)Math.PI);
        float f3 = -0.5F * f2;
        float f4 = 0.4F * Mth.sin(f1 * ((float)Math.PI * 2F));
        float f5 = -0.3F * Mth.sin(pSwingProgress * (float)Math.PI);
        poseStack.translate(f * f3, f4 - 0.3F * f2, f5);
        poseStack.rotateDegrees(Axis.XP, f2 * -45.0F);
        poseStack.rotateDegrees(Axis.YP, f * f2 * -30.0F);

        poseStack.rotateDegrees(Axis.YP, 180.0F);
        poseStack.rotateDegrees(Axis.ZP, 180.0F);
        poseStack.scale(0.38f, 0.38f, 0.38f);
        poseStack.translate(-0.5, -0.5, 0);
        poseStack.scale(1f, 1f, -1f);

        ExposureClient.photographRenderer().render(stack, true, false, poseStack, buffer, packedLight);

        poseStack.popPose();
    }

    @Unique
    private void exposure$renderTwoHandedPhotograph(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state,
                                                     PoseStack poseStack, SubmitNodeCollector buffer, int packedLight,
                                                     float pitch, float equippedProgress, float swingProgress) {
        float f = Mth.sqrt(swingProgress);
        float f1 = -0.2F * Mth.sin(swingProgress * (float)Math.PI);
        float f2 = -0.4F * Mth.sin(f * (float)Math.PI);
        poseStack.translate(0.0D, -f1 / 2.0F, f2);
        float f3 = this.calculateMapTilt(pitch);
        poseStack.translate(0.0D, 0.04F + equippedProgress * -1.2F + f3 * -0.5F, -0.72F);
        poseStack.rotateDegrees(Axis.XP, f3 * -85.0F);
        if (!playerState.avatarRenderState.isInvisible) {
            poseStack.pushPose();
            poseStack.rotateDegrees(Axis.YP, 90.0F);
            this.renderMapHand(poseStack, buffer, packedLight, HumanoidArm.RIGHT, playerState);
            this.renderMapHand(poseStack, buffer, packedLight, HumanoidArm.LEFT, playerState);
            poseStack.popPose();
        }

        float f4 = Mth.sin(f * (float)Math.PI);
        poseStack.rotateDegrees(Axis.XP, f4 * 20.0F);
        poseStack.scale(2.0F, 2.0F, 2.0F);

        poseStack.rotateDegrees(Axis.YP, 180.0F);
        poseStack.rotateDegrees(Axis.ZP, 180.0F);
        poseStack.scale(0.38f, 0.38f, 0.38f);
        poseStack.translate(-0.5, -0.5, 0);
        poseStack.scale(1f, 1f, -1f);

        ExposureClient.photographRenderer().render(state.mainHandItem, true, false, poseStack, buffer, packedLight);
    }
}
