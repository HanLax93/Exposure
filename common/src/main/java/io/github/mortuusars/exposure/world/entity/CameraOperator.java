package io.github.mortuusars.exposure.world.entity;

import io.github.mortuusars.exposure.world.camera.Camera;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Injected in Entities such as {@link net.minecraft.world.entity.player.Player}. <br>
 * Injected interfaces must have all methods as 'default'.
 */
public interface CameraOperator {
    static CameraOperator of(Entity entity) {
        return (CameraOperator) (Object) entity;
    }

    default @Nullable Camera getActiveExposureCamera() {
        throw new IllegalStateException("This method must be implemented.");
    }

    default void setActiveExposureCamera(Camera camera) {
        throw new IllegalStateException("This method must be implemented.");
    }

    default void removeActiveExposureCamera() {
        throw new IllegalStateException("This method must be implemented.");
    }

    // --

    default Optional<Camera> getActiveExposureCameraOptional() {
        return Optional.ofNullable(getActiveExposureCamera());
    }

    default float getExposureCameraActionAnim(float partialTick) {
        return 0F;
    }

    default LivingEntity asOperatorEntity() {
        return (LivingEntity) this;
    }
}
