package io.github.mortuusars.exposure.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.mortuusars.exposure.Config;
import io.github.mortuusars.exposure.world.item.BrokenInterplanarProjectorItem;
import io.github.mortuusars.exposure.world.item.InterplanarProjectorItem;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access, ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 27, 47, stack -> true)
                .withSlot(1, 76, 47, stack -> true)
                .withResultSlot(2, 134, 47)
                .build());
    }

    @WrapOperation(method = "setItemName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;validateName(Ljava/lang/String;)Ljava/lang/String;"))
    private String onSetItemName(String itemName, Operation<String> original) {
        if (Config.Server.SPEC.isLoaded()
              && Config.Server.INTERPLANAR_PROJECTOR_LARGER_RENAMING_LIMIT.get()
              && (inputSlots.getItem(0).getItem() instanceof InterplanarProjectorItem
                || inputSlots.getItem(0).getItem() instanceof BrokenInterplanarProjectorItem)) {
            String filtered = StringUtil.filterText(itemName);
            return filtered.length() <= 150 ? filtered : null;
        }

        return original.call(itemName);
    }
}
