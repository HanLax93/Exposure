package io.github.mortuusars.exposure.client.input;

import com.mojang.blaze3d.platform.InputConstants;

import java.util.function.*;

public record KeyBinding(Key matcher, Supplier<Boolean> handler) {
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        int keyCode = event.key();
        int scanCode = event.scancode();
        int modifiers = event.modifiers();
        return matchesPress(keyCode, scanCode, modifiers) && handler().get();
    }

    public boolean keyReleased(net.minecraft.client.input.KeyEvent event) {
        int keyCode = event.key();
        int scanCode = event.scancode();
        int modifiers = event.modifiers();
        return matchesRelease(keyCode, scanCode, modifiers) && handler().get();
    }

    public boolean matches(int keyCode, int scanCode, int press, int modifiers) {
        return matcher.matches(keyCode, scanCode, press, modifiers);
    }

    public boolean matchesPress(int keyCode, int scanCode, int modifiers) {
        return matcher.matches(keyCode, scanCode, InputConstants.PRESS, modifiers);
    }

    public boolean matchesRelease(int keyCode, int scanCode, int modifiers) {
        return matcher.matches(keyCode, scanCode, InputConstants.RELEASE, modifiers);
    }
}
