package io.github.mortuusars.exposure.client.gui.screen.test;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import io.github.mortuusars.exposure.client.util.Minecrft;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.function.Consumer;

public class Slider extends AbstractWidget {
    public static final Identifier SLIDER_SPRITE = Identifier.withDefaultNamespace("widget/slider");
    public static final Identifier HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("widget/slider_highlighted");
    public static final Identifier SLIDER_HANDLE_SPRITE = Identifier.withDefaultNamespace("widget/slider_handle");
    public static final Identifier SLIDER_HANDLE_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("widget/slider_handle_highlighted");

    protected static final int TEXT_MARGIN = 2;
    protected static final int HANDLE_WIDTH = 8;
    protected static final int HANDLE_HALF_WIDTH = 4;

    protected double position;
    protected boolean canChangeValue;

    protected final double defaultValue;
    protected final double min;
    protected final double max;
    protected final DecimalFormat displayFormat;
    protected final String name;
    protected final Consumer<Double> onChanged;

    @Nullable
    protected Pair<Integer, Integer> horizontalGradient;

    public Slider(int x, int y, int width, int height,
                  double defaultValue, double min, double max, int displayedPrecision, String name, Consumer<Double> onChanged) {
        this(x, y, width, height, CommonComponents.EMPTY, defaultValue, min, max, displayedPrecision, name, onChanged);
    }

    public Slider(int x, int y, int width, int height, Component message,
                  double defaultValue, double min, double max, int displayedPrecision, String name, Consumer<Double> onChanged) {
        super(x, y, width, height, message);
        this.position = (defaultValue - min) / (max - min);
        this.defaultValue = Mth.clamp(defaultValue, min, max);
        this.min = min;
        this.max = max;
        String zeros = "#".repeat(displayedPrecision);
        this.displayFormat = new DecimalFormat("#" + (zeros.isEmpty() ? "" : "." + zeros));
        this.name = name;
        this.onChanged = onChanged;
        updateMessage();
    }

    public Slider setHorizontalGradient(int leftColor, int rightColor) {
        this.horizontalGradient = Pair.of(leftColor, rightColor);
        return this;
    }

    public void removeHorizontalGradient() {
        this.horizontalGradient = null;
    }

    protected Identifier getSprite() {
        return this.isFocused() && !this.canChangeValue ? HIGHLIGHTED_SPRITE : SLIDER_SPRITE;
    }

    protected Identifier getHandleSprite() {
        return !this.isHovered && !this.canChangeValue ? SLIDER_HANDLE_SPRITE : SLIDER_HANDLE_HIGHLIGHTED_SPRITE;
    }

    // -- Value

    public double getPosition() {
        return this.position;
    }

    public void setPosition(double position) {
        double d = this.position;
        this.position = Mth.clamp(position, 0.0, 1.0);
        if (d != this.position) {
            this.applyValue();
        }

        this.updateMessage();
    }

    public Double getValue() {
        return Mth.clampedLerp(min, max, position);
    }

    public void setValue(double value) {
        double positionFromValue = (value - min) / (max - min);
        setPosition(positionFromValue);
    }

    public void resetToDefault() {
        setValue(defaultValue);
    }

    protected void updateMessage() {
        setMessage(Component.literal(name + ": " + displayFormat.format(getValue())));
    }

    protected void applyValue() {
        onChanged.accept(getValue());
    }

    // -- Input

    @Override
    public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        this.setPositionFromMouse(event.x());
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.canChangeValue = false;
        } else {
            InputType inputType = Minecraft.getInstance().getLastInputType();
            if (inputType == InputType.MOUSE || inputType == InputType.KEYBOARD_TAB) {
                this.canChangeValue = true;
            }
        }
    }

    protected void setPositionFromMouse(double mouseX) {
        this.setPosition((mouseX - (double)(this.getX() + HANDLE_HALF_WIDTH)) / (double)(this.width - HANDLE_WIDTH));
    }

    @Override
    public void onRelease(net.minecraft.client.input.MouseButtonEvent event) {
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (button == InputConstants.MOUSE_BUTTON_RIGHT && active && visible && isMouseOver(mouseX, mouseY)) {
            resetToDefault();
            playDownSound(Minecrft.get().getSoundManager());
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void onDrag(net.minecraft.client.input.MouseButtonEvent event, double dragX, double dragY) {
        this.setPositionFromMouse(event.x());
        super.onDrag(event, dragX, dragY);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        int keyCode = event.key();
        int scanCode = event.scancode();
        int modifiers = event.modifiers();
        if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_SPACE || keyCode == InputConstants.KEY_NUMPADENTER) {
            this.canChangeValue = !this.canChangeValue;
            return true;
        } else {
            if (this.canChangeValue) {
                boolean bl = keyCode == 263;
                if (bl || keyCode == 262) {
                    float f = bl ? -1.0F : 1.0F;
                    this.setPosition(position + (double)(f / (float)(width - HANDLE_WIDTH)));
                    return true;
                }
            }

            return false;
        }
    }

    // -- Render

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, getSprite(), getX(), getY(), getWidth(), getHeight(), alpha);

        if (active && horizontalGradient != null) {
            fillHorizontalGradient(guiGraphics, getX() + 1, getY() + 1, getX() + getWidth() - 1,
                    getY() + getHeight() - 1, horizontalGradient.getFirst(), horizontalGradient.getSecond());
        }

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, getHandleSprite(),
                getX() + (int)(position * (double)(width - HANDLE_WIDTH)), getY(), HANDLE_WIDTH, getHeight(), alpha);
        int textColor = (active ? 0xFFFFFF : 0xA0A0A0) | Mth.ceil(alpha * 255.0F) << 24;
        guiGraphics.centeredText(minecraft.font, getMessage(), getX() + getWidth() / 2,
                getY() + (getHeight() - 8) / 2, textColor);
    }

    private void fillHorizontalGradient(GuiGraphicsExtractor guiGraphics, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        guiGraphics.fillGradient(x1, y1, x2, y2, colorFrom, colorTo);
    }

    // --

    @Override
    protected @NotNull MutableComponent createNarrationMessage() {
        return Component.translatable("gui.narrate.slider", this.getMessage());
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.focused"));
            } else {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.hovered"));
            }
        }
    }
}
