package io.github.mortuusars.exposure.client.image.modifier.pixel;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class NegativeFilmEffect implements PixelEffect {
    @Override
    public String getIdentifier() {
        return "negative-film";
    }

    public int modify(int argb) {
        int alpha = ARGB.alpha(argb);
        int red = ARGB.red(argb);
        int green = ARGB.green(argb);
        int blue = ARGB.blue(argb);

        // Modify opacity to make lighter colors transparent, like in real film.
        int lightness = (red + green + blue) / 3;
        int opacity = (int) Mth.clamp(lightness * 1.5f, 0, 255);
        alpha = (alpha * opacity) / 255;

        // Invert
        red = 255 - red;
        green = 255 - green;
        blue = 255 - blue;

        return ARGB.color(alpha, red, green, blue);
    }
}
