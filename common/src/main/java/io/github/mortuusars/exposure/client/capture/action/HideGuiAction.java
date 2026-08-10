package io.github.mortuusars.exposure.client.capture.action;

import net.minecraft.client.Minecraft;

public class HideGuiAction implements CaptureAction {
    private boolean hideGuiBeforeCapture;

    @Override
    public void beforeCapture() {
        hideGuiBeforeCapture = Minecraft.getInstance().gui.hud.isHidden();
        if (!hideGuiBeforeCapture) Minecraft.getInstance().gui.hud.toggle();
    }

    @Override
    public void afterCapture() {
        if (Minecraft.getInstance().gui.hud.isHidden() != hideGuiBeforeCapture) {
            Minecraft.getInstance().gui.hud.toggle();
        }
    }
}
