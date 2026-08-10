package io.github.mortuusars.exposure.client.input;

import com.mojang.blaze3d.platform.InputConstants;

import java.util.ArrayList;
import java.util.Arrays;

public class KeyBindings {
    public static final KeyBindings EMPTY = new KeyBindings();

    protected final ArrayList<KeyBinding> bindings = new ArrayList<>();

    public void add(KeyBinding... bindings) {
        this.bindings.addAll(Arrays.asList(bindings));
    }

    public void add(int index, KeyBinding binding) {
        this.bindings.add(index, binding);
    }

    public boolean remove(KeyBinding binding) {
        return this.bindings.remove(binding);
    }

    public void clear() {
        this.bindings.clear();
    }

    // --

    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        int keyCode = event.key();
        int scanCode = event.scancode();
        int modifiers = event.modifiers();
        for (KeyBinding binding : bindings) {
            if (binding.matches(keyCode, scanCode, InputConstants.PRESS, modifiers) && binding.handler().get()) {
                return true;
            }
        }
        return false;
    }

    public boolean keyReleased(net.minecraft.client.input.KeyEvent event) {
        int keyCode = event.key();
        int scanCode = event.scancode();
        int modifiers = event.modifiers();
        for (KeyBinding binding : bindings) {
            if (binding.matches(keyCode, scanCode, InputConstants.RELEASE, modifiers) && binding.handler().get()) {
                return true;
            }
        }
        return false;
    }

    // --

    public static KeyBindings of(KeyBinding... bindings) {
        KeyBindings list = new KeyBindings();
        list.add(bindings);
        return list;
    }
}
