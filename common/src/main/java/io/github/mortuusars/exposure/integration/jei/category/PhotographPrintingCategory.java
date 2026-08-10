package io.github.mortuusars.exposure.integration.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.world.camera.ExposureType;
import io.github.mortuusars.exposure.integration.jei.ExposureJeiPlugin;
import io.github.mortuusars.exposure.integration.jei.recipe.PhotographPrintingJeiRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class PhotographPrintingCategory implements IRecipeCategory<PhotographPrintingJeiRecipe> {
    private static final Identifier TEXTURE = Exposure.resource("textures/gui/jei/photograph_printing.png");
    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic filmDrawable;

    public PhotographPrintingCategory(IGuiHelper guiHelper) {
        title = Component.translatable("jei.exposure.photograph_printing.title");
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 170, 80);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Exposure.Items.LIGHTROOM.get()));

        filmDrawable = guiHelper.createDrawable(TEXTURE, 0, 80, 170, 80);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, @NotNull PhotographPrintingJeiRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 78, 17)
                .addItemStack(new ItemStack(recipe.getExposureType() == ExposureType.COLOR ?
                        Exposure.Items.DEVELOPED_COLOR_FILM.get()
                        : Exposure.Items.DEVELOPED_BLACK_AND_WHITE_FILM.get()))
                .setSlotName("Film");


        List<ItemStack> papers = stacksIn(Exposure.Tags.Items.PHOTO_PAPERS);

        builder.addSlot(RecipeIngredientRole.INPUT, 6, 55)
                .addItemStacks(papers)
                .setSlotName("Paper");

        if (recipe.getExposureType() == ExposureType.COLOR) {
            List<ItemStack> cyanDyes = stacksIn(Exposure.Tags.Items.CYAN_PRINTING_DYES);

            builder.addSlot(RecipeIngredientRole.INPUT, 40, 55)
                    .addItemStacks(cyanDyes)
                    .setSlotName("Cyan");

            List<ItemStack> magentaDyes = stacksIn(Exposure.Tags.Items.MAGENTA_PRINTING_DYES);

            builder.addSlot(RecipeIngredientRole.INPUT, 58, 55)
                    .addItemStacks(magentaDyes)
                    .setSlotName("Magenta");

            List<ItemStack> yellowDyes = stacksIn(Exposure.Tags.Items.YELLOW_PRINTING_DYES);

            builder.addSlot(RecipeIngredientRole.INPUT, 76, 55)
                    .addItemStacks(yellowDyes)
                    .setSlotName("Yellow");
        }

        List<ItemStack> blackDyes = stacksIn(Exposure.Tags.Items.BLACK_PRINTING_DYES);

        builder.addSlot(RecipeIngredientRole.INPUT, 94, 55)
                .addItemStacks(blackDyes)
                .setSlotName("Black");

        ItemStack resultItemStack = new ItemStack(Exposure.Items.PHOTOGRAPH.get());
        resultItemStack.set(Exposure.DataComponents.PHOTOGRAPH_TYPE, recipe.getExposureType());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 144, 55).addItemStack(resultItemStack);
    }

    @Override
    public void draw(PhotographPrintingJeiRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
        filmDrawable.draw(guiGraphics);
    }

    private static List<ItemStack> stacksIn(TagKey<Item> tag) {
        List<ItemStack> stacks = new ArrayList<>();
        BuiltInRegistries.ITEM.getTagOrEmpty(tag).forEach(holder -> stacks.add(new ItemStack(holder.value())));
        return stacks;
    }

    @Override
    public @NotNull RecipeType<PhotographPrintingJeiRecipe> getRecipeType() {
        return ExposureJeiPlugin.PHOTOGRAPH_PRINTING_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return 170;
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }
}
