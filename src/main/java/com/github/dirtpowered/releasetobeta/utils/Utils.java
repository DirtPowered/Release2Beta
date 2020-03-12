package com.github.dirtpowered.releasetobeta.utils;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Utils {
    public static ItemStack betaItemStackToItemStack(BetaItemStack itemStack) {
        return itemStack == null ? new ItemStack(0) : new ItemStack(itemStack.getBlockId(), itemStack.getAmount(), itemStack.getData());
    }

    public static BetaItemStack itemStackToBetaItemStack(ItemStack itemStack) {
        return new BetaItemStack(itemStack.getId(), itemStack.getAmount(), itemStack.getData());
    }

    public static ItemStack[] convertItemStacks(BetaItemStack[] itemStacks) {
        List<ItemStack> list = new ArrayList<>();
        for (BetaItemStack item : itemStacks) {
            ItemStack itemStack;
            if (item != null)
                itemStack = new ItemStack(item.getBlockId(), item.getAmount(), item.getData());
            else
                itemStack = new ItemStack(0);

            list.add(itemStack);
        }
        return list.toArray(new ItemStack[0]);
    }

    public static double toModernPos(int pos) {
        return pos / 32.0D;
    }

    public static float toModernRotation(int rotation) {
        //TODO: calculate
        return rotation;
    }

    public static Float toFloat(double doubleValue) {
        return (float) doubleValue;
    }

    public static boolean isTileEntity(int blockId) {
        return blockId == 54; //TODO: add more
    }

    public static void debug(Object clazz) {
        Logger.info("[DEBUG] {}", ReflectionToStringBuilder.toString(clazz, ToStringStyle.SHORT_PREFIX_STYLE));
    }

    public static UUID getOfflineUUID(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes());
    }
}
