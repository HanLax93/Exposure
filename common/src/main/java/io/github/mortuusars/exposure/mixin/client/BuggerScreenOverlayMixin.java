package io.github.mortuusars.exposure.mixin.client;

import io.github.mortuusars.exposure.PlatformHelper;
import io.github.mortuusars.exposure.client.util.bugger.Bugger;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenOverlay.class)
public class BuggerScreenOverlayMixin {
    @SuppressWarnings("deprecation")
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void onRender(GuiGraphicsExtractor guiGraphics, CallbackInfo ci) {
        if (!PlatformHelper.isInDevEnv()) return;

        if (Bugger.page == 0) {
            Bugger.renderMainPage(guiGraphics);
            ci.cancel();
        }

        if (Bugger.page == 1) {
            Bugger.renderTagPage(guiGraphics);
            ci.cancel();
        }

        String str = "[<-] and [->] to switch pages";
        int strWidth = net.minecraft.client.Minecraft.getInstance().font.width(str);
        int x = guiGraphics.guiWidth() / 2 - strWidth / 2;
        guiGraphics.fill(x - 1, 1, x + strWidth + 1, 10, -1873784752);
        guiGraphics.text(net.minecraft.client.Minecraft.getInstance().font, str, x, 2, 0xFFFFFFFF, false);
    }
}
