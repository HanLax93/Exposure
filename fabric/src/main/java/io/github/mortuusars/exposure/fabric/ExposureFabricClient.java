package io.github.mortuusars.exposure.fabric;

import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.ExposureClient;
import io.github.mortuusars.exposure.client.gui.tooltip.CameraStandTooltip;
import io.github.mortuusars.exposure.client.input.KeyboardHandler;
import io.github.mortuusars.exposure.client.render.CameraStandEntityRenderer;
import io.github.mortuusars.exposure.client.render.GlassPhotographFrameEntityRenderer;
import io.github.mortuusars.exposure.fabric.resources.ExposureFabricClientReloadListener;
import io.github.mortuusars.exposure.client.gui.tooltip.PhotographClientTooltip;
import io.github.mortuusars.exposure.client.gui.screen.ItemRenameScreen;
import io.github.mortuusars.exposure.client.gui.screen.LightroomScreen;
import io.github.mortuusars.exposure.client.gui.screen.album.AlbumScreen;
import io.github.mortuusars.exposure.client.gui.screen.album.LecternAlbumScreen;
import io.github.mortuusars.exposure.client.gui.screen.camera.CameraAttachmentsScreen;
import io.github.mortuusars.exposure.integration.ModCompatibilityClient;
import io.github.mortuusars.exposure.world.inventory.tooltip.PhotographTooltip;
import io.github.mortuusars.exposure.network.fabric.FabricS2CPacketHandler;
import io.github.mortuusars.exposure.client.render.PhotographFrameEntityRenderer;
import io.github.mortuusars.exposure.world.item.camera.CameraItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class ExposureFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ExposureClient.init();

        ConfigScreenFactoryRegistry.INSTANCE.register(Exposure.ID, ConfigurationScreen::new);

        KeyboardHandler.registerKeymappings(KeyMappingHelper::registerKeyMapping);

        MenuScreens.register(Exposure.MenuTypes.CAMERA_IN_HAND.get(), CameraAttachmentsScreen::new);
        MenuScreens.register(Exposure.MenuTypes.CAMERA_ON_STAND.get(), CameraAttachmentsScreen::new);
        MenuScreens.register(Exposure.MenuTypes.ALBUM.get(), AlbumScreen::new);
        MenuScreens.register(Exposure.MenuTypes.LECTERN_ALBUM.get(), LecternAlbumScreen::new);
        MenuScreens.register(Exposure.MenuTypes.LIGHTROOM.get(), LightroomScreen::new);
        MenuScreens.register(Exposure.MenuTypes.ITEM_RENAME.get(), ItemRenameScreen::new);

        // The lightroom's film slot is intentionally rendered in a tab outside
        // the container panel. REI can consume clicks in that area before the
        // screen sees them, so handle only this tab at Fabric's input boundary.
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof LightroomScreen lightroomScreen) {
                ScreenMouseEvents.allowMouseClick(screen).register((currentScreen, event) ->
                        !lightroomScreen.handleFilmSlotTabClick(event));
                ScreenMouseEvents.allowMouseRelease(screen).register((currentScreen, event) ->
                        !lightroomScreen.handleFilmSlotTabRelease());
            }
        });


        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ExposureFabricClientReloadListener());

        EntityRendererRegistry.register(Exposure.EntityTypes.PHOTOGRAPH_FRAME.get(), PhotographFrameEntityRenderer::new);
        EntityRendererRegistry.register(Exposure.EntityTypes.CLEAR_PHOTOGRAPH_FRAME.get(), GlassPhotographFrameEntityRenderer::new);
        EntityRendererRegistry.register(Exposure.EntityTypes.CAMERA_STAND.get(), CameraStandEntityRenderer::new);

        ClientTooltipComponentCallback.EVENT.register(data -> data instanceof PhotographTooltip photographTooltip
                ? new PhotographClientTooltip(photographTooltip) : null);

        HudElementRegistry.addLast(Exposure.resource("camera_stand_tooltip"), CameraStandTooltip::render);

        FabricS2CPacketHandler.register();

        ModCompatibilityClient.handle();

        if (FabricLoader.getInstance().isDevelopmentEnvironment()
                && Boolean.getBoolean("exposure.auditMixins")) {
            MixinEnvironment.getCurrentEnvironment().audit();
        }
    }
}
