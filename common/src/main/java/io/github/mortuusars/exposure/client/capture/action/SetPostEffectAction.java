package io.github.mortuusars.exposure.client.capture.action;

import io.github.mortuusars.exposure.client.util.Minecrft;
import io.github.mortuusars.exposure.client.capture.CaptureShader;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class SetPostEffectAction implements CaptureAction {
    @Nullable
    private Identifier currentEffect;

    private final Identifier effect;

    public SetPostEffectAction(Identifier effect) {
        this.effect = effect;
    }

    @Override
    public void beforeCapture() {
        currentEffect = CaptureShader.currentLocation();
        CaptureShader.apply(effect);
    }

    @Override
    public void afterCapture() {
        if (currentEffect != null) {
            CaptureShader.apply(currentEffect);
        } else {
            CaptureShader.remove();
        }
    }
}
