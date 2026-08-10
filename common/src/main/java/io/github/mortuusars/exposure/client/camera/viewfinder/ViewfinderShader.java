package io.github.mortuusars.exposure.client.camera.viewfinder;

import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.client.util.Minecrft;
import io.github.mortuusars.exposure.world.camera.Camera;
import io.github.mortuusars.exposure.data.Filter;
import io.github.mortuusars.exposure.data.Filters;
import io.github.mortuusars.exposure.world.item.camera.Attachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ViewfinderShader implements AutoCloseable {
    private final Minecraft minecraft;
    private final Camera camera;
    private final Viewfinder viewfinder;

    @Nullable
    private PostChain shader;
    @Nullable
    private Identifier shaderLocation;
    private boolean active;

    public ViewfinderShader(Camera camera, Viewfinder viewfinder) {
        this.minecraft = Minecrft.get();
        this.camera = camera;
        this.viewfinder = viewfinder;
        this.update();
    }

    public void apply(Identifier shaderLocation) {
        if (shaderLocation.equals(this.shaderLocation)) return;
        shader = minecraft.getShaderManager().getPostChain(shaderLocation, Set.of(PostChain.MAIN_TARGET_ID));
        if (shader == null) {
            Exposure.LOGGER.warn("Failed to load shader: {}", shaderLocation);
            active = false;
            this.shaderLocation = null;
            return;
        }
        this.shaderLocation = shaderLocation;
        active = true;
    }

    public void resize(int width, int height) {
        // Post chains use the target dimensions when processed in 26.2.
    }

    /**
     * Processes current viewfinder shader (if it is present and active).
     */
    public void process() {
        if (shader != null && active) {
            shader.process(minecraft.gameRenderer.mainRenderTarget(), GraphicsResourceAllocator.UNPOOLED);
        }
    }

    public void update() {
        setActive(viewfinder.isLookingThrough());
        if (active) {
            ItemStack filterStack = Attachment.FILTER.get(camera.getItemStack()).getForReading();
            Filters.of(Minecrft.registryAccess(), filterStack).map(Filter::shader).ifPresentOrElse(this::apply, this::remove);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void remove() {
        shader = null;
        shaderLocation = null;
    }

    @Override
    public void close() {
        remove();
    }
}
