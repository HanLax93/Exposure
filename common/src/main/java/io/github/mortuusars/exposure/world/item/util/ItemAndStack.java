package io.github.mortuusars.exposure.world.item.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.*;

public class ItemAndStack<T extends Item> {
    private final T item;
    private final ItemStack stack;

    public ItemAndStack(ItemStack stack) {
        this.stack = stack;
        //noinspection unchecked
        this.item = (T) stack.getItem();
    }

    public T getItem() {
        return item;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public ItemAndStack<T> apply(BiConsumer<T, ItemStack> function) {
        function.accept(item, stack);
        return this;
    }

    public <R> R map(BiFunction<T, ItemStack, R> mappingFunction) {
        return mappingFunction.apply(item, stack);
    }

    @Override
    public String toString() {
        return "ItemAndStack{" + stack.toString() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemAndStack<?> that = (ItemAndStack<?>) o;
        return Objects.equals(item, that.item) && ItemStack.matches(stack, that.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, stack.getCount(), ItemStack.hashItemAndComponents(stack));
    }
}
