package io.github.mortuusars.exposure.world.item.crafting.recipe;

import io.github.mortuusars.exposure.Exposure;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ComponentTransferringRecipe extends CustomRecipe {
    private final Ingredient sourceIngredient;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStackTemplate result;
    private final CraftingBookCategory category;
    private final List<Ingredient> placementIngredients;
    private @Nullable PlacementInfo placementInfo;

    public ComponentTransferringRecipe(CraftingBookCategory category, Ingredient sourceIngredient,
                                       NonNullList<Ingredient> ingredients, ItemStackTemplate result) {
        super();
        this.category = category;
        this.sourceIngredient = sourceIngredient;
        this.ingredients = ingredients;
        this.result = result;
        this.placementIngredients = new ArrayList<>(ingredients.size() + 1);
        placementIngredients.add(sourceIngredient);
        placementIngredients.addAll(ingredients);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return (RecipeSerializer<? extends CustomRecipe>) Exposure.RecipeSerializers.COMPONENT_TRANSFERRING.get();
    }

    public @NotNull Ingredient getSourceIngredient() {
        return sourceIngredient;
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.create(placementIngredients);
        }
        return placementInfo;
    }

    public @NotNull ItemStackTemplate getResultTemplate() {
        return result;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (getSourceIngredient().isEmpty() || ingredients.isEmpty())
            return false;

        List<Ingredient> unmatchedIngredients = new ArrayList<>(ingredients);
        unmatchedIngredients.addFirst(getSourceIngredient());

        int itemsInCraftingGrid = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty())
                itemsInCraftingGrid++;

            if (itemsInCraftingGrid > ingredients.size() + 1)
                return false;

            if (!unmatchedIngredients.isEmpty()) {
                for (int j = 0; j < unmatchedIngredients.size(); j++) {
                    if (unmatchedIngredients.get(j).test(stack)) {
                        unmatchedIngredients.remove(j);
                        break;
                    }
                }
            }
        }

        return unmatchedIngredients.isEmpty() && itemsInCraftingGrid == ingredients.size() + 1;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput input) {
        for (int index = 0; index < input.size(); index++) {
            ItemStack itemStack = input.getItem(index);

            if (getSourceIngredient().test(itemStack)) {
                return transferComponents(itemStack, getResultTemplate().create());
            }
        }

        return getResultTemplate().create();
    }

    public @NotNull ItemStack transferComponents(ItemStack transferIngredientStack, ItemStack recipeResultStack) {
        // Only transfer components explicitly changed on the source stack. Applying the
        // complete component map also copies the source item's 26.2 prototype components,
        // including ITEM_NAME and ITEM_MODEL, making the correct result item look like the
        // undeveloped film it was created from.
        recipeResultStack.applyComponents(transferIngredientStack.getComponentsPatch());
        return recipeResultStack;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return ingredients.size() <= width * height;
    }
}
