package io.github.mortuusars.exposure.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jetbrains.annotations.Nullable;

public class CameraStandRenderState extends EntityRenderState {
    public final ItemStackRenderState stand = new ItemStackRenderState();
    public final ItemStackRenderState mount = new ItemStackRenderState();
    public final ItemStackRenderState camera = new ItemStackRenderState();
    public float yRot;
    public float xRot;
    public float hurtTime;
    public float damage;
    public int hurtDirection;
    public boolean malfunctioned;
    public float cameraScale;
    public @Nullable Float vehicleYRot;
}
