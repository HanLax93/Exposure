package io.github.mortuusars.exposure.world.inventory;

import io.github.mortuusars.exposure.Exposure;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Prediction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LecternAlbumMenu extends AbstractContainerMenu {
    public static final int BUTTON_PREV_PAGE = 1;
    public static final int BUTTON_NEXT_PAGE = 2;
    public static final int BUTTON_TAKE_BOOK = 3;
    public static final int BUTTON_PAGE_JUMP_RANGE_START = 100;

    private static final int SLOT_COUNT = 1;
    private static final int DATA_COUNT = 1;

    protected final Container container;
    protected final ContainerData data;

    public LecternAlbumMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(Exposure.MenuTypes.LECTERN_ALBUM.get(), containerId);
        this.container = container;
        this.data = data;

        checkContainerSize(container, SLOT_COUNT);
        checkContainerDataCount(data, DATA_COUNT);

        this.addSlot(new Slot(container, 0, -999, -999) {
            @Override
            public void setChanged() {
                super.setChanged();
                LecternAlbumMenu.this.slotsChanged(this.container);
            }
        });
        this.addDataSlots(data);
    }

    public LecternAlbumMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
        ItemStack book = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
        container.setItem(0, book);
    }


    public ItemStack getBook() {
        return this.container.getItem(0);
    }

    public int getPage() {
        return this.data.get(0);
    }

    @Override
    public void setData(int id, int data) {
        super.setData(id, data);
        this.broadcastChanges();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= BUTTON_PAGE_JUMP_RANGE_START) {
            int pageIndex = id - BUTTON_PAGE_JUMP_RANGE_START;
            this.setData(0, pageIndex);
            return true;
        }

        switch (id) {
            case BUTTON_PREV_PAGE: {
                int i = this.data.get(0);
                this.setData(0, i - 1);
                return true;
            }
            case BUTTON_NEXT_PAGE: {
                int i = this.data.get(0);
                this.setData(0, i + 1);
                return true;
            }
            case BUTTON_TAKE_BOOK: {
                if (!player.mayBuild()) {
                    return false;
                }

                ItemStack itemStack = this.container.removeItemNoUpdate(0);
                this.container.setChanged();
                if (!player.getInventory().add(itemStack)) {
                    player.drop(itemStack, false, Prediction.SERVER_ONLY);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }
}
