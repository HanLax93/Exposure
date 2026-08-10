package io.github.mortuusars.exposure.client.gui.tooltip;

import io.github.mortuusars.exposure.Config;
import io.github.mortuusars.exposure.client.util.Minecrft;
import io.github.mortuusars.exposure.world.entity.CameraStandEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;

public class CameraStandTooltip {
    public static void render(GuiGraphicsExtractor guiGraphics, DeltaTracker partialTick) {
        if (!Config.Client.CAMERA_STAND_TOOLTIP.get()) return;

        Minecraft minecraft = Minecrft.get();
        if (minecraft.gui.hud.isHidden()) return;
        if (minecraft.level == null || minecraft.player == null) return;
        if (minecraft.gui.screen() != null || !(minecraft.hitResult instanceof EntityHitResult entityHitResult)) return;
        if (!(entityHitResult.getEntity() instanceof CameraStandEntity stand)) return;
        if (stand.getCamera().isEmpty()) return;

        int x = minecraft.getWindow().getGuiScaledWidth() / 2 + 16;
        int y = minecraft.getWindow().getGuiScaledHeight() / 2 - 9;

        if (stand.isMalfunctioned()) {
            List<FormattedCharSequence> lines = Minecrft.get().font.split(Component.translatable("gui.exposure.camera_stand.tooltip.malfunctioned")
                    .withStyle(ChatFormatting.RED), 230);
            guiGraphics.setTooltipForNextFrame(minecraft.font, lines, x, y + 12);
        } else {
            guiGraphics.item(stand.getCamera(), x + 1, y + 1);

            guiGraphics.setTooltipForNextFrame(minecraft.font, stand.getCamera(), x + 16, y + 12);
        }
    }
}
