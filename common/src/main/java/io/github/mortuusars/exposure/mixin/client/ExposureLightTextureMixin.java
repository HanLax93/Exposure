package io.github.mortuusars.exposure.mixin.client;

import io.github.mortuusars.exposure.client.render.GammaModifier;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Lightmap.class)
public abstract class ExposureLightTextureMixin {
    @Inject(method = "getBrightness", at = @At(value = "RETURN"), cancellable = true)
    private static void modifyBrightness(DimensionType pDimensionType, int pLightLevel, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(GammaModifier.getModifiedValue(cir.getReturnValue()));
    }
}
