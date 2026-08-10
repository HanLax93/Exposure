package io.github.mortuusars.exposure.client.gui;

import io.github.mortuusars.exposure.client.gui.screen.PhotographScreen;
import io.github.mortuusars.exposure.client.gui.screen.album.AlbumViewScreen;
import io.github.mortuusars.exposure.client.util.Minecrft;
import io.github.mortuusars.exposure.world.item.PhotographItem;
import io.github.mortuusars.exposure.world.item.crafting.recipe.FilmDevelopingRecipe;
import io.github.mortuusars.exposure.world.item.crafting.recipe.PhotographCopyingRecipe;
import io.github.mortuusars.exposure.world.item.crafting.recipe.ComponentTransferringRecipe;
import io.github.mortuusars.exposure.world.item.util.ItemAndStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Consumer;

public class ClientGUI {
    public static void openPhotographScreen(List<ItemAndStack<PhotographItem>> photographs) {
        Minecrft.get().gui.setScreen(new PhotographScreen(photographs));
    }

    public static void openPhotographsScreenFromItem(int item) {
        Minecrft.get().gui.setScreen(new PhotographScreen(PhotographScreen.PhotographProvider.fromPhotographItem(item)));
    }

    public static void openAlbumViewScreen(ItemStack albumStack) {
        Minecrft.get().gui.setScreen(new AlbumViewScreen(AlbumViewScreen.AlbumAccess.fromItem(albumStack)));
    }

    public static void addFilmRollDevelopingTooltip(ItemStack filmStack, Item.TooltipContext tooltipContext,
                                                    @NotNull Consumer<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        addRecipeTooltip(filmStack, tooltipContext, tooltipComponents, isAdvanced,
                r -> r instanceof FilmDevelopingRecipe filmDevelopingRecipe
                        && filmDevelopingRecipe.getSourceIngredient().test(filmStack),
                "item.exposure.film_roll.tooltip.details.develop");
    }

    public static void addPhotographCopyingTooltip(ItemStack photographStack, Item.TooltipContext tooltipContext,
                                                   @NotNull Consumer<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        addRecipeTooltip(photographStack, tooltipContext, tooltipComponents, isAdvanced,
                r -> r instanceof PhotographCopyingRecipe photographCopyingRecipe
                        && photographCopyingRecipe.getSourceIngredient().test(photographStack),
                "item.exposure.photograph.tooltip.details.copy");
    }

    private static void addRecipeTooltip(ItemStack stack, Item.TooltipContext tooltipContext,
                                         @NotNull Consumer<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced,
                                         Predicate<CraftingRecipe> recipeFilter, String detailsKey) {
        if (Minecraft.getInstance().level == null) {
            return;
        }

        tooltipComponents.accept(Component.translatable("tooltip.exposure.hold_for_details"));
        if (!Minecrft.get().hasShiftDown()) {
            return;
        }

        if (Minecraft.getInstance().getSingleplayerServer() == null) {
            return;
        }

        Optional<NonNullList<Ingredient>> recipeIngredients = Minecraft.getInstance().getSingleplayerServer()
                .getRecipeManager()
                .getRecipes()
                .stream()
                .map(RecipeHolder::value)
                .filter(CraftingRecipe.class::isInstance)
                .map(CraftingRecipe.class::cast)
                .filter(recipeFilter)
                .findFirst()
                .filter(ComponentTransferringRecipe.class::isInstance)
                .map(ComponentTransferringRecipe.class::cast)
                .map(ComponentTransferringRecipe::getIngredients);

        if (recipeIngredients.isEmpty() || recipeIngredients.get().isEmpty())
            return;

        NonNullList<Ingredient> ingredients = recipeIngredients.get();

        tooltipComponents.accept(Component.empty());

        Style orange = Style.EMPTY.withColor(0xc7954b);
        Style yellow = Style.EMPTY.withColor(0xeeda78);

        tooltipComponents.accept(Component.translatable(detailsKey).withStyle(orange));

        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack[] stacks = ingredients.get(i).items()
                    .map(holder -> new ItemStack(holder.value()))
                    .toArray(ItemStack[]::new);

            if (stacks.length == 0)
                tooltipComponents.accept(Component.literal("  ").append(Component.literal("?").withStyle(yellow)));
            else if (stacks.length == 1)
                tooltipComponents.accept(Component.literal("  ").append(stacks[0].getHoverName().copy().withStyle(yellow)));
            else { // Cycle stacks if it's not one:
                int val = (int) Math.ceil((Minecraft.getInstance().level.getGameTime() + 10 * i) % (20f * stacks.length) / 20f);
                int index = Mth.clamp(val - 1, 0, stacks.length - 1);

                tooltipComponents.accept(Component.literal("  ").append(stacks[index].getHoverName().copy().withStyle(yellow)));
            }
        }
    }
}
