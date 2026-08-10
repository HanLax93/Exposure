package io.github.mortuusars.exposure.client.image.modifier.pixel;

import net.minecraft.util.ARGB;

public class NegativeEffect implements PixelEffect {
    @Override
    public String getIdentifier() {
        return "negative";
    }

    public int modify(int argb) {
        int alpha = ARGB.alpha(argb);
        int red = ARGB.red(argb);
        int green = ARGB.green(argb);
        int blue = ARGB.blue(argb);

        // Invert
        red = 255 - red;
        green = 255 - green;
        blue = 255 - blue;

        return ARGB.color(alpha, red, green, blue);
    }
}
