package io.github.mortuusars.exposure.client.capture.task;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.mortuusars.exposure.Config;
import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.ExposureClient;
import io.github.mortuusars.exposure.client.capture.CaptureShader;
import io.github.mortuusars.exposure.client.util.Minecrft;
import io.github.mortuusars.exposure.client.util.Shader;
import io.github.mortuusars.exposure.client.image.WrappedNativeImage;
import io.github.mortuusars.exposure.client.capture.Capture;
import io.github.mortuusars.exposure.client.image.Image;
import io.github.mortuusars.exposure.util.cycles.task.Result;
import io.github.mortuusars.exposure.util.cycles.task.Task;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.renderer.PostChain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.Set;

/**
 * Captures a screenshot without showing it on screen. Makes photographing a seamless experience™.
 */
public class BackgroundScreenshotCaptureTask extends Task<Result<Image>> {
    private static boolean capturing = false;
    private static @Nullable RenderTarget renderTarget = null;

    public static boolean isCapturing() {
        return capturing && renderTarget != null;
    }

    public static @NotNull RenderTarget getRenderTarget() {
        return Objects.requireNonNull(renderTarget);
    }

    // --

    @Override
    public CompletableFuture<Result<Image>> execute() {
        if (ExposureClient.shouldUseDirectCapture()) {
            Exposure.LOGGER.warn("BackgroundScreenshotCaptureMethod is used while incompatible mods are installed. " +
                    "Captured image most likely will not look as expected.");
        }

        Minecraft minecraft = Minecrft.get();

        renderTarget = new MainTarget(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(
                renderTarget.getColorTexture(), new org.joml.Vector4f(0.0F),
                renderTarget.getDepthTexture(), 0.0D);

        try {
            capturing = true;

            minecraft.gameRenderer.setRenderBlockOutline(false);
            minecraft.gameRenderer.renderLevel(minecraft.getDeltaTracker());

            applyShaderEffects(renderTarget);

            CompletableFuture<Result<Image>> result = new CompletableFuture<>();
            Screenshot.takeScreenshot(renderTarget,
                    nativeImage -> result.complete(Result.success(new WrappedNativeImage(nativeImage))));
            return result.whenComplete((ignored, throwable) -> cleanup(minecraft));
        } catch (Exception e) {
            Exposure.LOGGER.error("Couldn't capture image: ", e);
            cleanup(minecraft);
            return CompletableFuture.completedFuture(Result.error(Capture.ERROR_FAILED_GENERIC));
        }
    }

    private static void cleanup(Minecraft minecraft) {
        minecraft.gameRenderer.setRenderBlockOutline(true);
        if (renderTarget != null) renderTarget.destroyBuffers();
        renderTarget = null;
        capturing = false;
    }

    private void applyShaderEffects(RenderTarget renderTarget) {
        Minecraft minecraft = Minecraft.getInstance();
        @Nullable net.minecraft.resources.Identifier effectId = minecraft.gameRenderer.currentPostEffect();
        if (effectId != null) {
            @Nullable PostChain effect = minecraft.getShaderManager().getPostChain(effectId,
                    Set.of(PostChain.MAIN_TARGET_ID));
            if (effect != null) Shader.process(effect, renderTarget);
        }

        CaptureShader.process(renderTarget);
    }
}
