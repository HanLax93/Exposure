package io.github.mortuusars.exposure.client.render;

import net.minecraft.world.item.ItemStack;

public interface ItemFrameRenderStateAccess {
    ItemStack exposure$getPhotograph();

    void exposure$setPhotograph(ItemStack stack);
}
