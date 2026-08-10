package io.github.mortuusars.exposure.client.render.photograph;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.mortuusars.exposure.ExposureClient;
import io.github.mortuusars.exposure.client.image.renderable.RenderableImage;
import io.github.mortuusars.exposure.client.render.image.RenderCoordinates;
import io.github.mortuusars.exposure.client.render.texture.TextureRenderer;
import io.github.mortuusars.exposure.util.color.Color;
import io.github.mortuusars.exposure.world.item.PhotographItem;
import io.github.mortuusars.exposure.world.item.StackedPhotographsItem;
import io.github.mortuusars.exposure.world.camera.frame.Frame;
import io.github.mortuusars.exposure.world.item.util.ItemAndStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PhotographRenderer {
    public boolean extractStackedPhotographs(List<ItemAndStack<PhotographItem>> photographs,
                                             GuiGraphicsExtractor graphics, Color color) {
        if (photographs.isEmpty()) return false;

        for (int i = Math.min(2, photographs.size() - 1); i >= 0; i--) {
            ItemAndStack<PhotographItem> photograph = photographs.get(i);
            PhotographStyle style = PhotographStyle.of(photograph.getItemStack());
            float offset = getStackedPhotographOffset() * i;

            graphics.pose().pushMatrix();
            graphics.pose().translate(offset, offset);
            if (style.paperTexture() != ExposureClient.Textures.EMPTY) {
                graphics.blit(style.paperTexture(), 0, 0, 1, 1, 0, 1, 1, 0);
                // GUI elements in 26.2 are batched within a stratum. Keep the dynamic
                // photograph texture in a later stratum so the paper cannot be sorted
                // and rendered over it.
                graphics.nextStratum();
            }

            if (i == 0) {
                Frame frame = photograph.getItem().getFrame(photograph.getItemStack());
                RenderableImage image = style.process(ExposureClient.renderedExposures().getOrCreate(frame));
                graphics.pose().translate(0.0625f, 0.0625f);
                graphics.pose().scale(0.875f, 0.875f);
                ExposureClient.imageRenderer().extract(image, graphics, RenderCoordinates.DEFAULT, color);
                graphics.pose().popMatrix();
                return !image.isEmpty();
            }
            graphics.pose().popMatrix();
        }
        return false;
    }

    public boolean render(ItemStack itemStack, boolean renderPaper, boolean renderBackside, PoseStack poseStack,
                              SubmitNodeCollector collector, int packedLight) {
        return render(itemStack, renderPaper, renderBackside, poseStack, collector, packedLight, 255, 255, 255, 255);
    }

    public boolean render(ItemStack itemStack, boolean renderPaper, boolean renderBackside, PoseStack poseStack,
                              SubmitNodeCollector collector, int packedLight, int r, int g, int b, int a) {
        if (itemStack.getItem() instanceof PhotographItem photographItem)
            return renderPhotograph(poseStack, collector, photographItem, itemStack, renderPaper, renderBackside, packedLight, r, g, b, a);
        else if (itemStack.getItem() instanceof StackedPhotographsItem stackedPhotographsItem)
            return renderStackedPhotographs(stackedPhotographsItem, itemStack, poseStack, collector, packedLight, r, g, b, a);
        return false;
    }

    public boolean renderPhotograph(PoseStack poseStack, SubmitNodeCollector collector,
                                        PhotographItem photographItem, ItemStack photographStack,
                                        boolean renderPaper, boolean renderBackside, int packedLight, int r, int g, int b, int a) {

        PhotographStyle style = PhotographStyle.of(photographStack);

        Frame frame = photographItem.getFrame(photographStack);

        RenderableImage image = style.process(ExposureClient.renderedExposures().getOrCreate(frame));

        int paperRotation = frame.identifier().hashCode() % 4 * 90;

        if (renderPaper && style.paperTexture() != ExposureClient.Textures.EMPTY) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(paperRotation));
            poseStack.translate(-0.5f, -0.5f, 0);

            TextureRenderer.render(poseStack, collector, style.paperTexture(), packedLight, r, g, b, a);

            poseStack.popPose();

            if (renderBackside) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.translate(-0.5, 0, -0.5);

                poseStack.translate(0.5f, 0.5f, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(paperRotation));
                poseStack.translate(-0.5f, -0.5f, 0);

                TextureRenderer.render(poseStack, collector, style.paperTexture(),
                        packedLight, (int) (r * 0.85f), (int) (g * 0.85f), (int) (b * 0.85f), a);

                poseStack.popPose();
            }
        }

        if (renderPaper) {
            poseStack.pushPose();
            float offset = 0.0625f;
            poseStack.translate(offset, offset, 0.001);
            poseStack.scale(0.875f, 0.875f, 0.875f);
            ExposureClient.imageRenderer().render(image, poseStack, collector, RenderCoordinates.DEFAULT, packedLight, r, g, b, a);
            poseStack.popPose();
        } else {
            ExposureClient.imageRenderer().render(image, poseStack, collector, RenderCoordinates.DEFAULT, packedLight, r, g, b, a);
        }

        if (renderPaper && style.hasOverlayTexture()) {
            poseStack.pushPose();

            poseStack.translate(0.5f, 0.5f, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(paperRotation));
            poseStack.translate(-0.5f, -0.5f, 0);

            poseStack.translate(0, 0, 0.002);
            TextureRenderer.render(poseStack, collector, style.overlayTexture(), packedLight, r, g, b, a);
            poseStack.popPose();
        }

        return !image.isEmpty();
    }

    public boolean renderStackedPhotographs(StackedPhotographsItem stackedPhotographsItem, ItemStack stack,
                                                PoseStack poseStack, SubmitNodeCollector collector,
                                                int packedLight, int r, int g, int b, int a) {
        List<ItemAndStack<PhotographItem>> photographs = stackedPhotographsItem.getPhotographs(stack).photographsItemAndStacks();
        return renderStackedPhotographs(photographs, poseStack, collector, packedLight, r, g, b, a);
    }

    public boolean renderStackedPhotographs(List<ItemAndStack<PhotographItem>> photographs,
                                                PoseStack poseStack, SubmitNodeCollector collector,
                                                int packedLight, int r, int g, int b, int a) {
        if (photographs.isEmpty()) return false;

        boolean photographRendered = false;

        for (int i = 2; i >= 0; i--) {
            if (photographs.size() - 1 < i)
                continue;

            ItemAndStack<PhotographItem> photograph = photographs.get(i);

            // Top photograph:
            if (i == 0) {
                poseStack.pushPose();
                poseStack.translate(0, 0, 0.002);
                photographRendered = renderPhotograph(poseStack, collector, photograph.getItem(), photograph.getItemStack(),
                        true, false, packedLight, r, g, b, a);
                poseStack.popPose();
                break;
            }

            // Photographs below (only paper)
            float posOffset = getStackedPhotographOffset() * i;

            poseStack.pushPose();
            poseStack.translate(posOffset, posOffset, 0.002 - i / 1000f);

            poseStack.translate(0.5f, 0.5f, 0);

            Frame frame = photograph.getItem().getFrame(photograph.getItemStack());
            int rotation = frame.identifier().hashCode() % 4 * 90;
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));

            poseStack.translate(-0.5f, -0.5f, 0);

            float brightness = 1f - (getStackedBrightnessStep() * i);

            PhotographStyle photographStyle = PhotographStyle.of(photograph.getItemStack());

            TextureRenderer.render(poseStack, collector, photographStyle.paperTexture(),
                    packedLight, (int)(r * brightness), (int)(g * brightness), (int)(b * brightness), a);

            poseStack.popPose();
        }

        return photographRendered;
    }

    public float getStackedBrightnessStep() {
        return 0.2f;
    }

    public float getStackedPhotographOffset() {
        // 2 px / Texture size (64px) = 0.03125
        return 0.03125f;
    }
}
