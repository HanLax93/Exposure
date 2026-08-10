package io.github.mortuusars.exposure.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import io.github.mortuusars.exposure.client.camera.CameraClient;
import io.github.mortuusars.exposure.client.capture.CaptureShader;
import net.minecraft.client.renderer.PostChain;
import org.jetbrains.annotations.NotNull;


public class Shader {
    private static boolean suppressViewfinder = false;

    /**
     * Processes specified shader (if it is present and active) to a specified render target.
     * Shader is not modified in the process. Copy of the shader is created and resized to the render target dimensions.
     * Since this method creates a temp PostChain on every call, this probably should not be used when performance matters.
     * Main use for this is to apply a shader when capturing a photograph.
     */
    public static void process(@NotNull PostChain shader, @NotNull RenderTarget renderTarget) {
        shader.process(renderTarget, GraphicsResourceAllocator.UNPOOLED);
    }

    public static void setSuppressViewfinder(boolean suppress) {
        suppressViewfinder = suppress;
    }

    public static void processForGameRenderer() {
        if (!suppressViewfinder && CameraClient.viewfinder() != null) {
            CameraClient.viewfinder().shader().process();
        }

        if (CaptureShader.hasShader()) {
            CaptureShader.process();
        }
    }

    public static void resize(int width, int height) {
        if (CameraClient.viewfinder() != null) {
            CameraClient.viewfinder().shader().resize(width, height);
        }

        if (CaptureShader.hasShader()) {
            CaptureShader.resize(width, height);
        }
    }
}
