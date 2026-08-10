package io.github.mortuusars.exposure.integration.jade.component_provider;

import io.github.mortuusars.exposure.world.entity.PhotographFrameEntity;
import io.github.mortuusars.exposure.integration.jade.ExposureJadePlugin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IDisplayHelper;

public enum PhotographFrameProvider implements IEntityComponentProvider {
    INSTANCE;

    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (accessor.getEntity() instanceof PhotographFrameEntity photographFrame) {
            ItemStack item = photographFrame.getItem();
            if (!item.isEmpty()) {
                tooltip.add(IDisplayHelper.get().stripColor(item.getHoverName()));
            }
        }
    }

    public Identifier getUid() {
        return ExposureJadePlugin.PHOTOGRAPH_FRAME;
    }
}