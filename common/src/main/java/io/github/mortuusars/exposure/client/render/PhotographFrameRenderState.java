package io.github.mortuusars.exposure.client.render;

import io.github.mortuusars.exposure.client.image.renderable.RenderableImage;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PhotographFrameRenderState extends EntityRenderState {
    public final BlockModelRenderState frame = new BlockModelRenderState();
    public final ItemStackRenderState fallbackItem = new ItemStackRenderState();
    public Direction direction = Direction.NORTH;
    public ItemStack item = ItemStack.EMPTY;
    public @Nullable RenderableImage image;
    public int size;
    public int itemRotation;
    public int brightness;
    public float xRot;
    public float yRot;
    public boolean frameInvisible;
    public boolean glowing;
}
