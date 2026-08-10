package io.github.mortuusars.exposure.world.item.crafting.recipe.serializer;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mortuusars.exposure.world.item.crafting.recipe.ComponentTransferringRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

public final class ComponentTransferringRecipeSerializer {
    private ComponentTransferringRecipeSerializer() {
    }

    public static <T extends ComponentTransferringRecipe> RecipeSerializer<T> create(
            String recipeName, String sourceName, RecipeConstructor<T> constructor) {
        return new RecipeSerializer<>(createCodec(recipeName, sourceName, constructor), createStreamCodec(constructor));
    }

    private static <T extends ComponentTransferringRecipe> @NotNull MapCodec<T> createCodec(
            String recipeTypeName, String sourceIngredientName, RecipeConstructor<T> constructor) {
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category),
                        Ingredient.CODEC.fieldOf(sourceIngredientName).forGetter(ComponentTransferringRecipe::getSourceIngredient),
                        Ingredient.CODEC
                                .listOf()
                                .fieldOf("ingredients")
                                .flatXmap(
                                        list -> {
                                            if (list.isEmpty()) {
                                                return DataResult.error(() -> "No ingredients for %s recipe".formatted(recipeTypeName));
                                            } else {
                                                return list.size() > 9
                                                        ? DataResult.error(() -> ("Too many ingredients for %s recipe. Maximum is: %s".formatted(9, recipeTypeName)))
                                                        : DataResult.success(toNonNullList(list));
                                            }
                                        },
                                        DataResult::success
                                )
                                .forGetter(ComponentTransferringRecipe::getIngredients),
                        ItemStackTemplate.CODEC.fieldOf("result")
                                .forGetter(ComponentTransferringRecipe::getResultTemplate)
                ).apply(instance, constructor::create)
        );
    }

    private static <T extends ComponentTransferringRecipe> @NotNull StreamCodec<RegistryFriendlyByteBuf, T> createStreamCodec(
            RecipeConstructor<T> constructor) {
        return StreamCodec.composite(
                CraftingBookCategory.STREAM_CODEC, CraftingRecipe::category,
                Ingredient.CONTENTS_STREAM_CODEC, ComponentTransferringRecipe::getSourceIngredient,
                ByteBufCodecs.collection(NonNullList::createWithCapacity, Ingredient.CONTENTS_STREAM_CODEC), ComponentTransferringRecipe::getIngredients,
                ItemStackTemplate.STREAM_CODEC, ComponentTransferringRecipe::getResultTemplate,
                constructor::create);
    }

    private static NonNullList<Ingredient> toNonNullList(java.util.List<Ingredient> ingredients) {
        NonNullList<Ingredient> result = NonNullList.createWithCapacity(ingredients.size());
        result.addAll(ingredients);
        return result;
    }

    @FunctionalInterface
    public interface RecipeConstructor<T extends ComponentTransferringRecipe> {
        T create(CraftingBookCategory arg, Ingredient sourceIngredient,
                 NonNullList<Ingredient> ingredients, ItemStackTemplate result);
    }
}
