package io.github.mortuusars.exposure.world.item.crafting.recipe;

import io.github.mortuusars.exposure.Exposure;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

public class FilmDevelopingRecipe extends ComponentTransferringRecipe {
    public FilmDevelopingRecipe(CraftingBookCategory category, Ingredient filmIngredient,
                                NonNullList<Ingredient> ingredients, ItemStackTemplate result) {
        super(category, filmIngredient, ingredients, result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return (RecipeSerializer<? extends CustomRecipe>) Exposure.RecipeSerializers.FILM_DEVELOPING.get();
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remainingItems = super.getRemainingItems(input);

        for (int i = 0; i < input.size(); ++i) {
            ItemStack item = input.getItem(i);
            if (item.getItem() instanceof PotionItem && remainingItems.get(i).isEmpty()) {
                remainingItems.set(i, new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return remainingItems;
    }
}
