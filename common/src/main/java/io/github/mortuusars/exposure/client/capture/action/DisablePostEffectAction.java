package io.github.mortuusars.exposure.client.capture.action;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.List;

public class DisablePostEffectAction implements CaptureAction {
    private List<Identifier> requestedPostEffects = List.of();

    @Override
    public void beforeCapture() {
        List<Identifier> effects = Minecraft.getInstance().gameRenderer.getRequestedPostEffects();
        requestedPostEffects = List.copyOf(effects);
        effects.clear();
    }

    @Override
    public void afterCapture() {
        List<Identifier> effects = Minecraft.getInstance().gameRenderer.getRequestedPostEffects();
        effects.clear();
        effects.addAll(requestedPostEffects);
    }
}
