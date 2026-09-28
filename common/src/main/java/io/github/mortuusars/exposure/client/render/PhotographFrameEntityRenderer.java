package io.github.mortuusars.exposure.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.mortuusars.exposure.Config;
import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.ExposureClient;
import io.github.mortuusars.exposure.client.image.modifier.ImageEffect;
import io.github.mortuusars.exposure.client.image.renderable.RenderableImage;
import io.github.mortuusars.exposure.client.render.image.RenderCoordinates;
import io.github.mortuusars.exposure.client.render.photograph.PhotographStyle;
import io.github.mortuusars.exposure.world.camera.frame.Frame;
import io.github.mortuusars.exposure.world.entity.GlassPhotographFrameEntity;
import io.github.mortuusars.exposure.world.entity.PhotographFrameEntity;
import io.github.mortuusars.exposure.world.item.PhotographItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class PhotographFrameEntityRenderer<T extends PhotographFrameEntity>
        extends EntityRenderer<T, PhotographFrameRenderState> {
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

    private final ItemModelResolver itemModelResolver;
    private final BlockModelResolver blockModelResolver;

    public PhotographFrameEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
        this.blockModelResolver = context.getBlockModelResolver();
    }

    @Override
    public PhotographFrameRenderState createRenderState() {
        return new PhotographFrameRenderState();
    }

    @Override
    public void extractRenderState(T entity, PhotographFrameRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        if (Minecraft.getInstance().hitResult instanceof EntityHitResult hit && hit.getEntity() == entity) {
            Minecraft.getInstance().crosshairPickEntity = entity;
        }

        state.direction = entity.getDirection();
        state.size = entity.getSize();
        state.xRot = entity.getXRot();
        state.yRot = entity.getYRot();
        state.itemRotation = entity.getItemRotation();
        state.frameInvisible = entity.isFrameInvisible();
        state.glowing = entity.isGlowing();
        state.brightness = state.glowing ? 255 : getPhotographBrightness(entity);
        state.item = entity.getItem().copy();
        state.image = null;

        if (!state.frameInvisible) {
            // 26.2 renders hanging frames through block models (like vanilla ItemFrame).
            // Load the per-size block model via BlockModelResolver.
            blockModelResolver.update(state.frame, frameBlockFor(entity), BLOCK_DISPLAY_CONTEXT);
        } else {
            state.frame.clear();
        }

        if (state.item.getItem() instanceof PhotographItem photographItem) {
            PhotographStyle style = PhotographStyle.of(state.item);
            Frame frame = photographItem.getFrame(state.item);
            RenderableImage image = style.process(ExposureClient.renderedExposures().getOrCreate(frame));
            if (Config.Client.PIXEL_PERFECT_PHOTOGRAPH_FRAME.get()) {
                int pixels = 16 * (state.size + 1) - (state.frameInvisible ? 0 : 4);
                image = image.modifyWith(ImageEffect.Resize.to(pixels)::modify, "pixels-" + pixels);
            }
            state.image = image;
            state.fallbackItem.clear();
        } else {
            itemModelResolver.updateForNonLiving(state.fallbackItem,
                    state.item, ItemDisplayContext.FIXED, entity);
        }
    }

    @Override
    public void submit(PhotographFrameRenderState state, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        super.submit(state, poseStack, collector, cameraRenderState);

        Direction direction = state.direction;
        poseStack.pushPose();
        double hangOffset = 0.46875;
        poseStack.translate(direction.getStepX() * hangOffset,
                direction.getStepY() * hangOffset, direction.getStepZ() * hangOffset);
        poseStack.mulPose(Axis.XP.rotationDegrees(state.xRot));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.yRot));

        int lightCoords = state.glowing ? LightCoordsUtil.FULL_BRIGHT : state.lightCoords;
        if (state.image != null) {
            submitPhotograph(state, poseStack, collector, lightCoords);
        } else if (!state.fallbackItem.isEmpty()) {
            poseStack.pushPose();
            float scale = 0.65f + state.size * 0.5f;
            poseStack.translate(0, 0, 0.46875);
            poseStack.scale(scale, scale, scale * 0.75f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(state.itemRotation * 90.0F));
            state.fallbackItem.submit(poseStack, collector, lightCoords,
                    OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.popPose();
        }

        if (!state.frame.isEmpty()) {
            poseStack.pushPose();
            // The block model is drawn relative to the block origin (0,0,0), while the
            // photograph is drawn centred on the entity. Shift the frame so its centre
            // (0.5, 0.5, ~1.0 in block units) aligns with the entity centre.
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            state.frame.submit(poseStack, collector, state.lightCoords,
                    OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private BlockState frameBlockFor(T entity) {
        boolean glass = entity instanceof GlassPhotographFrameEntity;
        Block block = switch (entity.getSize()) {
            case 0 -> glass ? Exposure.Blocks.GLASS_PHOTOGRAPH_FRAME_SMALL.get()
                            : Exposure.Blocks.PHOTOGRAPH_FRAME_SMALL.get();
            case 1 -> glass ? Exposure.Blocks.GLASS_PHOTOGRAPH_FRAME_MEDIUM.get()
                            : Exposure.Blocks.PHOTOGRAPH_FRAME_MEDIUM.get();
            default -> glass ? Exposure.Blocks.GLASS_PHOTOGRAPH_FRAME_LARGE.get()
                            : Exposure.Blocks.PHOTOGRAPH_FRAME_LARGE.get();
        };
        return block.defaultBlockState();
    }

    private void submitPhotograph(PhotographFrameRenderState state, PoseStack poseStack,
                                  SubmitNodeCollector collector, int lightCoords) {
        poseStack.pushPose();
        float border = state.frameInvisible ? 0f : 0.125f;
        float z = (state.frameInvisible ? 0.497f : 0.48f)
                - Config.Client.PHOTOGRAPH_FRAME_IMAGE_OFFSET.get().floatValue();
        float size = state.size + 1 - border * 2;
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.itemRotation * 90.0F + 180.0F));
        poseStack.translate(-0.5 * (state.size + 1) + border,
                -0.5 * (state.size + 1) + border, z);
        poseStack.scale(size, size, 1);
        ExposureClient.imageRenderer().render(state.image, poseStack, collector,
                RenderCoordinates.DEFAULT, lightCoords,
                state.brightness, state.brightness, state.brightness, 255);
        poseStack.popPose();
    }

    public int getPhotographBrightness(T entity) {
        if (entity.getDirection() == Direction.UP) return 255;
        int lightLevel = entity.level().getBrightness(LightLayer.BLOCK, entity.blockPosition());
        float shade = 1.0f;
        shade += (1f - shade) * 0.2f;
        int shaded = (int) (255 * shade);
        return Math.min(255, shaded + (int) ((255 - shaded) * (lightLevel / 15f * 0.5f)));
    }

    @Override
    protected boolean shouldShowName(T entity, double distanceToCameraSq) {
        float range = entity.isDiscrete() ? 32.0f : 64.0f;
        return super.shouldShowName(entity, distanceToCameraSq)
                && !entity.getItem().isEmpty()
                && entity.getItem().has(DataComponents.CUSTOM_NAME)
                && Minecraft.getInstance().crosshairPickEntity == entity
                && distanceToCameraSq < range * range;
    }

    @Override
    protected @NotNull Component getNameTag(T entity) {
        return entity.getItem().getHoverName();
    }
}
