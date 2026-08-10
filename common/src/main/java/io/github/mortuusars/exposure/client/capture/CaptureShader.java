package io.github.mortuusars.exposure.client.capture;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.client.util.Minecrft;
import io.github.mortuusars.exposure.client.util.Shader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class CaptureShader {
    @Nullable
    private static Identifier shaderLocation = null;

    public static boolean hasShader() {
        return shaderLocation != null;
    }

    public static @Nullable Identifier currentLocation() {
        return shaderLocation;
    }

    public static void apply(Identifier shaderLocation) {
        if (shaderLocation.equals(CaptureShader.shaderLocation)) return;
        PostChain chain = Minecrft.get().getShaderManager().getPostChain(shaderLocation,
                Set.of(PostChain.MAIN_TARGET_ID));
        if (chain == null) {
            Exposure.LOGGER.warn("Failed to load shader: {}", shaderLocation);
            remove();
            return;
        }
        CaptureShader.shaderLocation = shaderLocation;
    }

    public static void resize(int width, int height) {
        // Post chains use the target dimensions when processed in 26.2.
    }

    public static void process() {
        if (shaderLocation == null) return;
        PostChain chain = Minecrft.get().getShaderManager().getPostChain(shaderLocation,
                Set.of(PostChain.MAIN_TARGET_ID));
        if (chain != null) chain.process(Minecrft.get().gameRenderer.mainRenderTarget(), GraphicsResourceAllocator.UNPOOLED);
    }

    /**
     * Processes current shader (if it is present and active) to a specified render target.
     * Current shader is not modified in the process. Copy of the shader is created and resized to the render target dimensions.
     * Since this method creates a temp PostChain on every call, this probably should not be used when performance matters.
     * Main use for this is to apply a shader when capturing a photograph.
     */
    public static void process(RenderTarget renderTarget) {
        if (shaderLocation == null) return;
        PostChain chain = Minecrft.get().getShaderManager().getPostChain(shaderLocation,
                Set.of(PostChain.MAIN_TARGET_ID));
        if (chain != null) process(chain, renderTarget);
    }

    /**
     * Processes specified shader (if it is present and active) to a specified render target.
     * Shader is not modified in the process. Copy of the shader is created and resized to the render target dimensions.
     * Since this method creates a temp PostChain on every call, this probably should not be used when performance matters.
     * Main use for this is to apply a shader when capturing a photograph.
     */
    public static void process(@NotNull PostChain shader, @NotNull RenderTarget renderTarget) {
        shader.process(renderTarget, GraphicsResourceAllocator.UNPOOLED);
    }

    public static void remove() {
        shaderLocation = null;
    }
}
