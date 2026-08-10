package io.github.mortuusars.exposure.integration.jade.component_provider;

import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.world.block.entity.Lightroom;
import io.github.mortuusars.exposure.world.block.entity.LightroomBlockEntity;
import io.github.mortuusars.exposure.world.lightroom.PrintingMode;
import io.github.mortuusars.exposure.integration.jade.ExposureJadePlugin;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.JadeUI;
import snownee.jade.api.view.*;

import java.util.Collections;
import java.util.List;

public enum LightroomComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        CompoundTag tag = accessor.getServerData();

        if (tag.getBooleanOr("Empty", false))
            return;

        tooltip.add(JadeUI.spacer(0, 0));

        ItemStack film = readStack(tag, "Film");
        if (!film.isEmpty()) {
            tooltip.append(JadeUI.item(film));
            tooltip.append(JadeUI.text(Component.literal("|").withStyle(ChatFormatting.GRAY)));
        }

        ItemStack paper = readStack(tag, "Paper");
        if (!paper.isEmpty()) {
            tooltip.append(JadeUI.item(paper));
            tooltip.append(JadeUI.text(Component.literal("+").withStyle(ChatFormatting.GRAY)));
        }

        for (String dye : new String[] {"Cyan", "Yellow", "Magenta", "Black"}) {
            ItemStack stack = readStack(tag, dye);
            if (!stack.isEmpty())
                tooltip.append(JadeUI.item(stack));
        }

        tooltip.append(JadeUI.progressArrow(tag.getFloatOr("Progress", 0.0f)));

        tooltip.append(JadeUI.item(readStack(tag, "Result")));


        PrintingMode process = PrintingMode.fromStringOrDefault(tag.getStringOr("Process", ""), PrintingMode.REGULAR);
        if (process != PrintingMode.REGULAR)
            tooltip.add(JadeUI.text(Component.translatable("gui.exposure.lightroom.printing_mode." + process.getSerializedName())));

        tooltip.add(JadeUI.spacer(0, 2));
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof LightroomBlockEntity lightroomBlockEntity) {
            if (lightroomBlockEntity.isEmpty()) {
                tag.putBoolean("Empty", true);
                return;
            }

            storeStack(tag, "Film", lightroomBlockEntity.getItem(Lightroom.FILM_SLOT));
            storeStack(tag, "Paper", lightroomBlockEntity.getItem(Lightroom.PAPER_SLOT));
            storeStack(tag, "Cyan", lightroomBlockEntity.getItem(Lightroom.CYAN_SLOT));
            storeStack(tag, "Yellow", lightroomBlockEntity.getItem(Lightroom.YELLOW_SLOT));
            storeStack(tag, "Magenta", lightroomBlockEntity.getItem(Lightroom.MAGENTA_SLOT));
            storeStack(tag, "Black", lightroomBlockEntity.getItem(Lightroom.BLACK_SLOT));
            storeStack(tag, "Result", lightroomBlockEntity.getItem(Lightroom.RESULT_SLOT));

            tag.putString("Process", lightroomBlockEntity.getActualPrintingMode().getSerializedName());

            tag.putFloat("Progress", lightroomBlockEntity.getProgressPercentage());
        }
    }

    private static ItemStack readStack(CompoundTag tag, String key) {
        return tag.read(key, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
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

    public static class EmptyItemStackExtensionProvider implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
        public static final Identifier ID = Exposure.resource("empty");

        public static final EmptyItemStackExtensionProvider INSTANCE = new EmptyItemStackExtensionProvider();

        private EmptyItemStackExtensionProvider() {

        }

        @Override
        public @Nullable List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
            return Collections.emptyList();
        }

        @Override
        public Identifier getUid() {
            return ID;
        }

        @Override
        public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> list) {
            return Collections.emptyList();
        }
    }
}
