/*
 * Copyright (c) 2020 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.releasetobeta.data.inventory;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import lombok.Getter;
import lombok.Setter;

public class PlayerInventory {
    private ItemStack[] inventoryItems = new ItemStack[90];

    @Getter
    protected ItemStack[] armorItems = new ItemStack[9];

    @Getter
    private int currentSlot;

    @Getter
    @Setter
    private int lastSlot;

    @Getter
    @Setter
    private ItemStack lastClickedItem;

    public ItemStack getItem(int i) {
        return this.inventoryItems[i];
    }

    public void setItem(int i, ItemStack itemstack) {
        if (i > 4 && i < 9)
            this.armorItems[i] = itemstack;

        this.inventoryItems[i] = itemstack;
    }

    public ItemStack[] getItems() {
        return inventoryItems.clone();
    }

    public void setItems(ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            setItem(i, items[i]);
        }
    }

    public void setCurrentHotbarSlot(int currentSlot) {
        this.currentSlot = 36 + currentSlot;
    }

    public ItemStack getItemInHand() {
        return inventoryItems[currentSlot];
    }
}
