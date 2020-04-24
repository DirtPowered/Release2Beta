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
                itemStack = new ItemStack(session.remapBlock(item.getBlockId()), item.getAmount(), session.remapMetadata(item.getBlockId(), item.getData()));
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
        return ((yaw % 360) * 360) / 256F;
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

    public static String toBetaChatColors(String message) {
        String replacement = "§f";
        return message
                .replaceAll("§l", replacement)
                .replaceAll("§m", replacement)
                .replaceAll("§n", replacement)
                .replaceAll("§o", replacement);
    }
}
