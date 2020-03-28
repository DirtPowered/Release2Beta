package com.github.dirtpowered.releasetobeta.utils;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
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

    public static ItemStack[] convertItemStacks(BetaClientSession session, BetaItemStack[] itemStacks) {
        List<ItemStack> list = new ArrayList<>();
        for (BetaItemStack item : itemStacks) {
            ItemStack itemStack;
            if (item != null)
                itemStack = new ItemStack(session.remapBlock(item.getBlockId()), item.getAmount(), item.getData());
            else
                itemStack = new ItemStack(0);

            list.add(itemStack);
        }
        return list.toArray(new ItemStack[0]);
    }

    public static double toModernPos(int pos) {
        return pos / 32.0D;
    }

    public static float toModernPitch(int pitch) {
        /* -90 is looking straight up, and 90 is looking straight down. */
        return ((pitch % 360) * 360) / 256F;
    }

    public static float toModernYaw(int yaw) {
        return (float) yaw; //TODO: calculate
    }

    public static Float toFloat(double doubleValue) {
        return (float) doubleValue;
    }

    public static void debug(Object clazz) {
        Logger.info("[DEBUG] {}", ReflectionToStringBuilder.toString(clazz, ToStringStyle.SHORT_PREFIX_STYLE));
    }

    public static int fromChunkPos(int chunkPos) {
        return chunkPos * 16;
    }

    public static UUID getOfflineUUID(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes());
    }
}
