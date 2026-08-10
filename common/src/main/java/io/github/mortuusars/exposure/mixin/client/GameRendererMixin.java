package io.github.mortuusars.exposure.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.mortuusars.exposure.ExposureClient;
import io.github.mortuusars.exposure.client.capture.task.BackgroundScreenshotCaptureTask;
import com.mojang.blaze3d.pipeline.RenderTarget;
import io.github.mortuusars.exposure.client.util.Shader;
import io.github.mortuusars.exposure.event.ClientEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class, priority = 500)
public abstract class GameRendererMixin {
    @ModifyExpressionValue(method = {"render", "renderLevel"}, at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/renderer/GameRenderer;mainRenderTarget:Lcom/mojang/blaze3d/pipeline/RenderTarget;"))
    private RenderTarget exposure$redirectCaptureTarget(RenderTarget original) {
        return BackgroundScreenshotCaptureTask.isCapturing()
                ? BackgroundScreenshotCaptureTask.getRenderTarget()
                : original;
    }

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;)V",
            shift = At.Shift.AFTER))
    void onRender(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        // Processing viewfinder shader should be done before capturing
        // otherwise Direct capture method will not be affected by it.
        Shader.processForGameRenderer();
        ExposureClient.cycles().tick();
    }

    @Inject(method = "resize", at = @At(value = "HEAD"))
    void onResize(int width, int height, CallbackInfo ci) {
        Shader.resize(width, height);
    }

    @Inject(method = "resetData", at = @At(value = "RETURN"))
    void onResetData(CallbackInfo ci) {
        ClientEvents.resetRenderData();
    }
}
