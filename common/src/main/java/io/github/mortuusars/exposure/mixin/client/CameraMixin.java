package io.github.mortuusars.exposure.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mortuusars.exposure.client.camera.CameraClient;
import io.github.mortuusars.exposure.client.render.FovModifier;
import io.github.mortuusars.exposure.world.item.camera.CameraItem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void move(float zoom, float dy, float dx);
    @Shadow
    protected abstract void setRotation(float yRot, float xRot);
    @Shadow
    private float xRot;
    @Shadow
    private float yRot;
    @Shadow
    private boolean detached;

    @Inject(method = "getMaxZoom", at = @At(value = "RETURN"), cancellable = true)
    private void getMaxZoom(float maxZoom, CallbackInfoReturnable<Float> cir) {
        if (CameraClient.viewfinder() != null && CameraClient.viewfinder().isLookingThrough()) {
            cir.setReturnValue(Math.min(CameraClient.viewfinder().selfie().getMaxCameraDistance(), cir.getReturnValue()));
        }
    }

    @ModifyReturnValue(method = "calculateFov", at = @At("RETURN"))
    private float modifyFov(float original) {
        return (float) FovModifier.modify(original);
    }

    @Inject(method = "alignWithEntity", at = @At(value = "RETURN"))
    private void onAlignWithEntity(float partialTick, CallbackInfo ci) {
        if (CameraClient.viewfinder() != null && CameraClient.viewfinder().isLookingThrough()) {
            if (detached && Minecraft.getInstance().options.getCameraType().isMirrored()
                    && CameraClient.viewfinder().camera().inSelfieMode()) {
                setRotation((float) (yRot + CameraClient.viewfinder().selfie().getCameraYRot()),
                        (float) (xRot + CameraClient.viewfinder().selfie().getCameraXRot()));
            }

            if (!detached) {
                float yOffset = (CameraClient.viewfinder().camera()
                        .map(CameraItem::getYPositionOffset)
                        .orElse(0.0)
                        .floatValue());
                move(0, yOffset, 0);
            }
        }
    }
}
