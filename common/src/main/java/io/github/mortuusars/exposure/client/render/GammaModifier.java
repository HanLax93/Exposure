package io.github.mortuusars.exposure.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class GammaModifier {
    private static float offset = 0f;

    public static float getOffset() {
        return offset;
    }

    public static void apply(float offsetValue) {
        offsetValue = Mth.clamp(offsetValue, -1F, 1F);
        if (offset != offsetValue) {
            offset = offsetValue;
            // Update light texture immediately:
        }
    }

    public static void restore() {
        if (offset != 0f) {
            offset = 0f;
            // Update light texture immediately:
        }
    }

    public static float getModifiedValue(float original) {
        return original + offset;
    }
}
