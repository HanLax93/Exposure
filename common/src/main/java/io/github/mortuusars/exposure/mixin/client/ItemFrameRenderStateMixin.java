package io.github.mortuusars.exposure.mixin.client;

import io.github.mortuusars.exposure.client.render.ItemFrameRenderStateAccess;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemFrameRenderState.class)
public class ItemFrameRenderStateMixin implements ItemFrameRenderStateAccess {
    @Unique
    private ItemStack exposure$photograph = ItemStack.EMPTY;

    @Override
    public ItemStack exposure$getPhotograph() {
        return exposure$photograph;
    }

    @Override
    public void exposure$setPhotograph(ItemStack stack) {
        exposure$photograph = stack;
    }
}
