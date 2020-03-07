package com.github.dirtpowered.releasetobeta.utils;

import com.github.dirtpowered.betaprotocollib.utils.BetaItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;

public class Utils {
    public static ItemStack betaItemStackToItemStack(BetaItemStack itemStack) {
        return new ItemStack(itemStack.getBlockId(), itemStack.getAmount(), itemStack.getData());
    }
}
