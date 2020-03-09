package com.github.dirtpowered.releasetobeta.data.inventory;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;

public class Slot {
    private int index;
    private BetaItemStack itemStack;

    public Slot(int index, BetaItemStack itemStack) {
        this.index = index;
        this.itemStack = itemStack;
    }

    public int getIndex() {
        return index;
    }

    public BetaItemStack getItemStack() {
        return itemStack;
    }
}
