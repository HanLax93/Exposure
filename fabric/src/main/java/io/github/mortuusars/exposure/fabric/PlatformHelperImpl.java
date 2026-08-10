package io.github.mortuusars.exposure.fabric;

import io.github.mortuusars.exposure.fabric.api.event.ModifyEntityInFrameExtraDataCallback;
import io.github.mortuusars.exposure.fabric.api.event.FrameAddedCallback;
import io.github.mortuusars.exposure.fabric.api.event.ModifyFrameExtraDataCallback;
import io.github.mortuusars.exposure.util.ExtraData;
import io.github.mortuusars.exposure.world.camera.capture.CaptureParameters;
import io.github.mortuusars.exposure.world.camera.frame.Frame;
import io.github.mortuusars.exposure.world.entity.CameraHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class PlatformHelperImpl {
    public static @Nullable MinecraftServer getServer() {
        return ExposureFabric.server;
    }

    public static boolean canShear(ItemStack stack) {
        return stack.getItem() instanceof ShearsItem;
    }

    public static boolean canStrip(ItemStack stack) {
        return stack.getItem() instanceof AxeItem;
    }

    public static void openMenu(ServerPlayer serverPlayer, MenuProvider menuProvider, Consumer<RegistryFriendlyByteBuf> extraDataWriter) {
        ExtendedMenuProvider<byte[]> extendedScreenHandlerFactory = new ExtendedMenuProvider<byte[]>() {
            @Override
            public byte[] getScreenOpeningData(ServerPlayer player) {
                RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess());
                extraDataWriter.accept(buffer);
                byte[] bytes = ByteBufUtil.getBytes(buffer);
                buffer.release();
                return bytes;
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
                return menuProvider.createMenu(i, inventory, player);
            }

            @Override
            public @NotNull Component getDisplayName() {
                return menuProvider.getDisplayName();
            }
        };

        serverPlayer.openMenu(extendedScreenHandlerFactory);
    }

    public static List<String> getDefaultSpoutDevelopmentColorSequence() {
        return List.of(
                "{FluidName:\"create:potion\",Amount:27000,Tag:{Potion:\"minecraft:awkward\"}}",
                "{FluidName:\"create:potion\",Amount:27000,Tag:{Potion:\"minecraft:thick\"}}",
                "{FluidName:\"create:potion\",Amount:27000,Tag:{Potion:\"minecraft:mundane\"}}");
    }

    public static List<String> getDefaultSpoutDevelopmentBWSequence() {
        return List.of(
                "{FluidName:\"minecraft:water\",Amount:27000}");
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    /**
     * This method is here because on forge checking if mod is loaded at mixin apply time is different (LoadingModList vs ModList)
     * But on fabric we can use the same code.
     */
    public static boolean isModLoading(String modId) {
        return isModLoaded(modId);
    }

    public static boolean isInDevEnv() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    // --

    public static boolean isCreateDeployer(Player player, InteractionHand hand) {
        return false;
    }

    // --

    public static void postModifyEntityInFrameExtraDataEvent(CameraHolder cameraHolder, ItemStack camera, LivingEntity entityInFrame, ExtraData data) {
        ModifyEntityInFrameExtraDataCallback.EVENT.invoker().modifyEntityInFrameData(cameraHolder, camera, entityInFrame, data);
    }

    public static void postModifyFrameExtraDataEvent(CameraHolder cameraHolder, ItemStack camera, CaptureParameters captureParameters,
                                                List<BlockPos> positionsInFrame, List<LivingEntity> entitiesInFrame, ExtraData data) {
        ModifyFrameExtraDataCallback.EVENT.invoker().modifyFrameExtraData(cameraHolder, camera, captureParameters, positionsInFrame, entitiesInFrame, data);
    }

    public static void postFrameAddedEvent(CameraHolder cameraHolder, ItemStack camera, Frame frame,
                                           List<BlockPos> positionsInFrame, List<LivingEntity> entitiesInFrame) {
        FrameAddedCallback.EVENT.invoker().frameAdded(cameraHolder, camera, frame, positionsInFrame, entitiesInFrame);
    }
}
