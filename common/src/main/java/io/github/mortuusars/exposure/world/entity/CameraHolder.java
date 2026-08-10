package io.github.mortuusars.exposure.world.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Injected in Entities such as {@link net.minecraft.world.entity.player.Player} and others in the future. <br>
 * Injected interfaces must have all methods as 'default'.
 */
public interface CameraHolder {
    static CameraHolder of(Entity entity) {
        return (CameraHolder) (Object) entity;
    }

    /**
     * Player that captures the image (renders it). If missing - photo should not be taken.
     */
    default Optional<Player> getPlayerExecutingExposure() {
        throw new IllegalStateException("This method must be implemented, " +
                "and should return a player that will render the image.");
    }

    default Optional<ServerPlayer> getServerPlayerExecutingExposure() {
        return getPlayerExecutingExposure()
                .filter(pl -> pl instanceof ServerPlayer)
                .map(pl -> (ServerPlayer) pl);
    }

    /**
     * Used to trigger advancements and award stats.
     */
    default Optional<Player> getPlayerAwardedForExposure() {
        throw new IllegalStateException("This method must be implemented, " +
                "and should return a player that will receive advancements or stats for exposure (if applicable).");
    }

    default Optional<ServerPlayer> getServerPlayerAwardedForExposure() {
        return getPlayerAwardedForExposure()
                .filter(pl -> pl instanceof ServerPlayer)
                .map(pl -> (ServerPlayer) pl);
    }

    /**
     * Entity that will be treated as an author of a photo.
     */
    default @NotNull Entity getExposureAuthorEntity() {
        throw new IllegalStateException("This method must be implemented, " +
                "and should return an entity that will be treated as an author of a photo.");
    }

    default Optional<CameraOperator> getExposureCameraOperator() {
        throw new IllegalStateException("This method must be implemented, and should return an operator (if present).");
    }

    // --

    default Entity asHolderEntity() {
        return ((Entity) this);
    }
}
