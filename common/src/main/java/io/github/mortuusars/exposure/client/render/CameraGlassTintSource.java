package io.github.mortuusars.exposure.client.render;

import com.mojang.serialization.MapCodec;
import io.github.mortuusars.exposure.world.item.camera.CameraItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum CameraGlassTintSource implements ItemTintSource {
    INSTANCE;

    public static final MapCodec<CameraGlassTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        return CameraItem.getGlassTintColor(stack, 1);
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
