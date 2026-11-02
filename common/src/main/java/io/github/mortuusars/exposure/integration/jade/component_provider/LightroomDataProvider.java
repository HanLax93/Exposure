package io.github.mortuusars.exposure.integration.jade.component_provider;

import io.github.mortuusars.exposure.integration.jade.ExposureJadePlugin;
import io.github.mortuusars.exposure.world.block.entity.Lightroom;
import io.github.mortuusars.exposure.world.block.entity.LightroomBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum LightroomDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof LightroomBlockEntity lightroom)) {
            return;
        }

        if (lightroom.isEmpty()) {
            tag.putBoolean("Empty", true);
            return;
        }

        storeStack(tag, "Film", lightroom.getItem(Lightroom.FILM_SLOT));
        storeStack(tag, "Paper", lightroom.getItem(Lightroom.PAPER_SLOT));
        storeStack(tag, "Cyan", lightroom.getItem(Lightroom.CYAN_SLOT));
        storeStack(tag, "Yellow", lightroom.getItem(Lightroom.YELLOW_SLOT));
        storeStack(tag, "Magenta", lightroom.getItem(Lightroom.MAGENTA_SLOT));
        storeStack(tag, "Black", lightroom.getItem(Lightroom.BLACK_SLOT));
        storeStack(tag, "Result", lightroom.getItem(Lightroom.RESULT_SLOT));
        tag.putString("Process", lightroom.getActualPrintingMode().getSerializedName());
        tag.putFloat("Progress", lightroom.getProgressPercentage());
    }

    private static void storeStack(CompoundTag tag, String key, ItemStack stack) {
        tag.store(key, ItemStack.OPTIONAL_CODEC, stack);
    }

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        return true;
    }

    @Override
    public Identifier getUid() {
        return ExposureJadePlugin.LIGHTROOM;
    }
}
