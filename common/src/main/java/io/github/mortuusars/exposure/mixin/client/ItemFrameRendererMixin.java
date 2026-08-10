package io.github.mortuusars.exposure.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.mortuusars.exposure.Config;
import io.github.mortuusars.exposure.client.render.ItemFramePhotographRenderer;
import io.github.mortuusars.exposure.client.render.ItemFrameRenderStateAccess;
import io.github.mortuusars.exposure.world.item.PhotographItem;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameRendererMixin<T extends ItemFrame>
        extends EntityRenderer<T, ItemFrameRenderState> {
    protected ItemFrameRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void exposure$extractPhotograph(T entity, ItemFrameRenderState state,
                                             float partialTick, CallbackInfo ci) {
        ItemStack stack = entity.getItem();
        ItemStack photograph = ItemStack.EMPTY;
        if (Config.Client.PHOTOGRAPH_RENDERS_IN_ITEM_FRAME.get()
                && stack.getItem() instanceof PhotographItem item
                && !item.getFrame(stack).identifier().isEmpty()) {
            photograph = stack.copy();
            state.item.clear();
        }
        ((ItemFrameRenderStateAccess) state).exposure$setPhotograph(photograph);
    }

    @Inject(method = "submit", at = @At("TAIL"))
    private void exposure$submitPhotograph(ItemFrameRenderState state, PoseStack poseStack,
                                            SubmitNodeCollector collector,
                                            CameraRenderState cameraRenderState, CallbackInfo ci) {
        ItemStack stack = ((ItemFrameRenderStateAccess) state).exposure$getPhotograph();
        if (stack.isEmpty() || !(stack.getItem() instanceof PhotographItem photographItem)) return;

        poseStack.pushPose();
        Vec3 offset = getRenderOffset(state);
        poseStack.translate(-offset.x, -offset.y, -offset.z);
        Direction direction = state.direction;
        poseStack.translate(direction.getStepX() * 0.46875,
                direction.getStepY() * 0.46875, direction.getStepZ() * 0.46875);
        float xRot = direction.getAxis().isHorizontal()
                ? 0
                : -90 * direction.getAxisDirection().getStep();
        float yRot = direction.getAxis().isHorizontal() ? 180 - direction.toYRot() : 180;
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.translate(0, 0, state.isInvisible ? 0.5f : 0.4375f);
        ItemFramePhotographRenderer.render(state, poseStack, collector,
                state.lightCoords, photographItem, stack);
        poseStack.popPose();
    }
}
