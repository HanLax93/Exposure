package io.github.mortuusars.exposure.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.exposure.client.input.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"),
            cancellable = true)
    private void onScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci,
                  @Local(ordinal = 4 /* Magic number that corresponds to yScroll variable*/) double yScroll) {
        if (yScroll != 0 && MouseHandler.scrolled(yScroll)) {
            ci.cancel();
        }
    }

    @Inject(method = "onButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;overlay()Lnet/minecraft/client/gui/screens/Overlay;",
            ordinal = 0), cancellable = true)
    private void onPress(long windowPointer, MouseButtonInfo button, int action, CallbackInfo ci) {
        if (MouseHandler.buttonPressed(button.button(), action, button.modifiers()))
            ci.cancel();
    }

    @ModifyVariable(method = "turnPlayer", at = @At(value = "STORE"), ordinal = 3)
    private double modifySensitivity(double sensitivity) {
        return MouseHandler.modifySensitivity(sensitivity);
    }
}
