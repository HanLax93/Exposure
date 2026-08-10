package io.github.mortuusars.exposure.client.gui.tooltip;

import io.github.mortuusars.exposure.ExposureClient;
import io.github.mortuusars.exposure.world.item.PhotographItem;
import io.github.mortuusars.exposure.world.inventory.tooltip.PhotographTooltip;
import io.github.mortuusars.exposure.world.item.util.ItemAndStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import io.github.mortuusars.exposure.util.color.Color;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PhotographClientTooltip implements ClientTooltipComponent {
    public static final int SIZE = 72;

    protected final PhotographTooltip tooltip;
    protected final List<ItemAndStack<PhotographItem>> photographs;

    public PhotographClientTooltip(PhotographTooltip tooltip) {
        this.tooltip = tooltip;
        this.photographs = tooltip.photographs();
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return SIZE;
    }

    @Override
    public int getHeight(Font font) {
        return SIZE + 2; // 2px bottom margin
    }

    @Override
    public void extractImage(@NotNull Font font, int mouseX, int mouseY, int width, int height, GuiGraphicsExtractor guiGraphics) {
        int photographsCount = photographs.size();
        int additionalPhotographs = Math.min(2, photographsCount - 1);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(mouseX, mouseY);
        float scale = SIZE;
        float nextPhotographOffset = ExposureClient.photographRenderer().getStackedPhotographOffset();
        scale *= 1f - (additionalPhotographs * nextPhotographOffset);
        guiGraphics.pose().scale(scale, scale);

        ExposureClient.photographRenderer().extractStackedPhotographs(photographs, guiGraphics, Color.WHITE);

        guiGraphics.pose().popMatrix();

        // Stack count:
        if (photographsCount > 1) {
            guiGraphics.nextStratum();
            guiGraphics.pose().pushMatrix();
            String count = Integer.toString(photographsCount);
            int fontWidth = Minecraft.getInstance().font.width(count);
            float fontScale = 1.6f;
            guiGraphics.pose().translate(
                    mouseX + scale - 2 - fontWidth * fontScale,
                    mouseY + scale - 2 - 8 * fontScale);
            guiGraphics.pose().scale(fontScale, fontScale);
            guiGraphics.text(font, count, 0, 0, 0xFFFFFFFF);
            guiGraphics.pose().popMatrix();
        }
    }
}
