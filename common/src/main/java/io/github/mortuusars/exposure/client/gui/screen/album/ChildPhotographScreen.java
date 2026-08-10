package io.github.mortuusars.exposure.client.gui.screen.album;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.client.gui.screen.PhotographScreen;
import io.github.mortuusars.exposure.client.util.Minecrft;
import io.github.mortuusars.exposure.world.item.PhotographItem;
import io.github.mortuusars.exposure.world.item.util.ItemAndStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChildPhotographScreen extends PhotographScreen {
    private final Screen parentScreen;

    public ChildPhotographScreen(Screen parentScreen, List<ItemAndStack<PhotographItem>> photographs) {
        super(photographs);
        this.parentScreen = parentScreen;
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        if (zoom.get() < zoom.getMin() + 0.1f && zoom.getTarget() < zoom.getMin() + 0.1f) {
            Minecrft.get().gui.setScreen(parentScreen);
            Minecrft.player().playSound(Exposure.SoundEvents.PHOTOGRAPH_PLACE.get(), 0.7f, 1.1f);
        }
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        int keyCode = event.key();
        int scanCode = event.scancode();
        int modifiers = event.modifiers();
        if (keyCode == InputConstants.KEY_ESCAPE || Minecrft.options().keyInventory.matches(event)) {
            onClose();
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }

        if (button == InputConstants.MOUSE_BUTTON_RIGHT) {
            zoom.setTarget(0f);
            return true;
        }

        return false;
    }

    @Override
    public void onClose() {
        zoom.setTarget(0f); // ChildPhotographScreen#render will close screen when zooming out ends.
    }
}
