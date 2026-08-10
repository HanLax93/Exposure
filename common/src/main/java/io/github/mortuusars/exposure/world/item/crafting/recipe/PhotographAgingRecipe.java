package io.github.mortuusars.exposure.world.item.crafting.recipe;

import io.github.mortuusars.exposure.Exposure;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class PhotographAgingRecipe extends ComponentTransferringRecipe {
    public PhotographAgingRecipe(CraftingBookCategory category, Ingredient sourceIngredient,
                                 NonNullList<Ingredient> ingredients, ItemStackTemplate result) {
        super(category, sourceIngredient, ingredients, result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return (RecipeSerializer<? extends CustomRecipe>) Exposure.RecipeSerializers.PHOTOGRAPH_AGING.get();
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remainingItems = super.getRemainingItems(input);

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof BrushItem) {
                stack = stack.copy();
                int damage = stack.getDamageValue() + 1;
                stack.setDamageValue(damage);
                if (damage >= stack.getMaxDamage()) {
                    stack.shrink(1);
                }
                remainingItems.set(i, stack);
            }
        }

        return remainingItems;
    }
}
