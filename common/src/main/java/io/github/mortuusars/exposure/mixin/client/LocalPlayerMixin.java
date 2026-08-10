package io.github.mortuusars.exposure.mixin.client;

import com.mojang.authlib.GameProfile;
import io.github.mortuusars.exposure.client.camera.CameraClient;
import io.github.mortuusars.exposure.world.camera.Camera;
import io.github.mortuusars.exposure.world.camera.CameraOnStand;
import io.github.mortuusars.exposure.world.entity.CameraOperator;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {
    @Unique
    private Camera exposure$previousCamera;

    public LocalPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        CameraClient.tick();

        CameraOperator operator = CameraOperator.of((LocalPlayer) (Object) this);
        Camera camera = operator.getActiveExposureCamera();
        if (camera != null && !camera.isActive()) {
            operator.removeActiveExposureCamera();
            camera = null;
        }

        if (camera != exposure$previousCamera) {
            if (camera == null) CameraClient.removeViewfinder();
            else CameraClient.setupViewfinder(camera);
            exposure$previousCamera = camera;
        }
    }
}
