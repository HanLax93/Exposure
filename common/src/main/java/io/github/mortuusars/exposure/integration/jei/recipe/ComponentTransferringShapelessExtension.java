package io.github.mortuusars.exposure.integration.jei.recipe;

import io.github.mortuusars.exposure.world.item.crafting.recipe.ComponentTransferringRecipe;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.ArrayList;
import java.util.List;

public class ComponentTransferringShapelessExtension implements ICraftingCategoryExtension<ComponentTransferringRecipe> {
    @Override
    public List<SlotDisplay> getIngredients(RecipeHolder<ComponentTransferringRecipe> recipeHolder) {
        ComponentTransferringRecipe recipe = recipeHolder.value();
        List<SlotDisplay> inputs = new ArrayList<>();
        inputs.add(recipe.getSourceIngredient().display());
        recipe.getIngredients().forEach(ingredient -> inputs.add(ingredient.display()));
        return inputs;
    }
}
