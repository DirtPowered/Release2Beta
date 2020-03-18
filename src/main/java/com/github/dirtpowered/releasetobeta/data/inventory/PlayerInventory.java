package com.github.dirtpowered.releasetobeta.data.inventory;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;

public class PlayerInventory {
    private ItemStack[] inventoryItems = new ItemStack[90];
    private int currentSlot;

    public ItemStack getItem(int i) {
        ItemStack[] inv = this.inventoryItems;
        return inv[i];
    }

    private void setItem(int i, ItemStack itemstack) {
        ItemStack[] inv = this.inventoryItems;
        inv[i] = itemstack;
    }

    public ItemStack[] getItems() {
        return inventoryItems.clone();
    }

    public void setItems(ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            setItem(i, item);
        }
    }

    public void setCurrentSlot(int currentSlot) {
        this.currentSlot = 36 + currentSlot;
    }

    public ItemStack getItemInHand() {
        return inventoryItems[currentSlot];
    }
}
