package io.github.mortuusars.exposure.fabric.mixin;

import io.github.mortuusars.exposure.client.util.Minecrft;
import io.github.mortuusars.exposure.world.entity.CameraStandEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Ensures the controlling player is visible while the camera is attached to a stand. */
@Mixin(LevelExtractor.class)
public abstract class LevelRendererMixin {
    @Shadow
    private EntityRenderState extractEntity(Entity entity, float partialTick) {
        throw new AssertionError();
    }

    @Inject(method = "extractVisibleEntities", at = @At("TAIL"))
    private void exposure$extractControllingPlayer(Camera camera, Frustum frustum, DeltaTracker deltaTracker,
                                                    LevelRenderState renderState, CallbackInfo ci) {
        if (camera.entity() instanceof CameraStandEntity && Minecrft.get().player != null) {
            float partialTick = deltaTracker.getGameTimeDeltaPartialTick(
                    !Minecrft.level().tickRateManager().isEntityFrozen(Minecrft.get().player));
            renderState.entityRenderStates.add(extractEntity(Minecrft.get().player, partialTick));
        }
    }
}
