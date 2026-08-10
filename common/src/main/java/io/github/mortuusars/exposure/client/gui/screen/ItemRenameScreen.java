package io.github.mortuusars.exposure.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.client.gui.Widgets;
import io.github.mortuusars.exposure.world.inventory.ItemRenameMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemRenameScreen extends AbstractContainerScreen<ItemRenameMenu> {
    public static final Identifier TEXTURE = Exposure.resource("textures/gui/item_rename.png");

    protected EditBox name;

    public ItemRenameScreen(ItemRenameMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 206, 66);
    }

    @Override
    protected void init() {
        inventoryLabelX = -999;
        inventoryLabelY = -999;
        titleLabelX = 28;
        super.init();

        name = new EditBox(this.font, leftPos + 32, topPos + 21, 142, 12, Component.translatable("gui.exposure.item_rename.title"));
        name.setTextColor(-1);
        name.setTextColorUneditable(-1);
        name.setBordered(false);
        name.setMaxLength(ItemRenameMenu.MAX_NAME_LENGTH);
        name.setResponder(this::onNameChanged);
        name.setValue(getMenu().getItemName());
        addWidget(name);
        setInitialFocus(name);

        ImageButton applyButton = new ImageButton(leftPos + 133, topPos + 42, 19, 19,
                Widgets.CONFIRM_BUTTON_SPRITES,
                button -> confirm(), Component.translatable("gui.exposure.item_rename.apply"));
        applyButton.setTooltip(Tooltip.create(Component.translatable("gui.exposure.item_rename.apply")));
        addRenderableWidget(applyButton);

        ImageButton cancelButton = new ImageButton(leftPos + 154, topPos + 42, 19, 19,
                Widgets.CANCEL_BUTTON_SPRITES,
                button -> cancel(), Component.translatable("gui.exposure.item_rename.cancel"));
        cancelButton.setTooltip(Tooltip.create(Component.translatable("gui.exposure.item_rename.cancel")));
        addRenderableWidget(cancelButton);
    }

    protected void confirm() {
        getMenu().clickMenuButton(Minecraft.getInstance().player, ItemRenameMenu.APPLY_BUTTON_ID);
        Objects.requireNonNull(Minecraft.getInstance().gameMode).handleInventoryButtonClick(getMenu().containerId, ItemRenameMenu.APPLY_BUTTON_ID);
        onClose();
    }

    protected void cancel() {
        onClose();
    }

    @Override
    public void resize(int width, int height) {
        String string = this.name.getValue();
        super.resize(width, height);
        this.name.setValue(string);
    }

//    @Override
//    public void containerTick() {
//        super.containerTick();
//        name.tick();
//    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        this.name.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        renderBg(guiGraphics, partialTick, mouseX, mouseY);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        super.extractTooltip(guiGraphics, x, y);
    }

    protected void renderBg(GuiGraphicsExtractor guiGraphics, float partialTick, int mouseX, int mouseY) {
        io.github.mortuusars.exposure.client.util.GuiUtil.blit(guiGraphics, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    protected void onNameChanged(String name) {
        ItemStack itemStack = getMenu().getSlot(0).getItem();
        if (!itemStack.has(DataComponents.CUSTOM_NAME) && name.equals(itemStack.getHoverName().getString())
                || name.equals(itemStack.getItem().getName(itemStack).getString())) {
            name = "";
        }

        if (getMenu().setItemName(name) && Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.connection.send(new ServerboundRenameItemPacket(name));
        }
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        int keyCode = event.key();
        int scanCode = event.scancode();
        int modifiers = event.modifiers();
        if (keyCode == 256) {
            onClose();
        }

        if (keyCode == InputConstants.KEY_RETURN) {
            confirm();
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1f));
            return true;
        }

        if (keyCode != InputConstants.KEY_TAB && (name.keyPressed(event) || name.canConsumeInput())) {
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (button == InputConstants.MOUSE_BUTTON_RIGHT && name.isMouseOver(mouseX, mouseY)) {
            name.setValue("");
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }
}
