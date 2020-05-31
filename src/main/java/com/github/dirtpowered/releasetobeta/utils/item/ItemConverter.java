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

package com.github.dirtpowered.releasetobeta.utils.item;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.ShortTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ItemConverter {

    public static ItemStack betaToModern(BetaClientSession session, BetaItemStack item) {
        if (item == null)
            return new ItemStack(0);

        item.setBlockId(session.remapBlock(item.getBlockId(), item.getData(), true));
        item.setData(session.remapMetadata(item.getBlockId(), item.getData()));

        if (MinecraftVersion.B_1_9.isNewerOrEqual(R2BConfiguration.version) && item.hasNbt()) {
            com.mojang.nbt.CompoundTag itemTag = item.getNbt();
            if (itemTag.contains("ench")) {
                com.mojang.nbt.ListTag listTag = itemTag.getList("ench");
                System.out.println(listTag.size());

                CompoundTag rootTag = new CompoundTag(StringUtils.EMPTY);
                rootTag.put(new ListTag("AttributeModifiers", Collections.emptyList()));
                rootTag.put(new ListTag("ench", CompoundTag.class));

                for (int i = 0; i < listTag.size(); i++) {
                    com.mojang.nbt.CompoundTag compoundTag = (com.mojang.nbt.CompoundTag) listTag.get(i);

                    short enchantLevel = compoundTag.getShort("lvl");
                    short enchantId = compoundTag.getShort("id");

                    ListTag compoundList = rootTag.get("ench");
                    CompoundTag valueHolder = new CompoundTag(StringUtils.EMPTY);

                    //seems that nothing really was changed in enchant ids since beta
                    valueHolder.put(new ShortTag("id", enchantId));
                    valueHolder.put(new ShortTag("lvl", enchantLevel));

                    compoundList.add(valueHolder);
                }
                return new ItemStack(item.getBlockId(), item.getAmount(), item.getData(), rootTag);
            } else {
                //never happens (not sure)
                return null;
            }
        } else {
            return new ItemStack(item.getBlockId(), item.getAmount(), item.getData(), removeItemAttributes());
        }
    }

    public static BetaItemStack itemStackToBetaItemStack(ItemStack itemStack) {
        return new BetaItemStack(itemStack.getId(), itemStack.getAmount(), itemStack.getData());
    }

    public static ItemStack[] betaToModern(BetaClientSession session, BetaItemStack[] items) {
        ItemStack[] is = new ItemStack[items.length];

        for (int i = 0; i < items.length; i++) {
            BetaItemStack item = items[i];
            is[i] = betaToModern(session, item);
        }

        return is;
    }

    private static CompoundTag removeItemAttributes() {
        Map<String, Tag> nbt = new HashMap<>();
        nbt.put("AttributeModifiers", new ListTag("AttributeModifiers", Collections.emptyList()));
        return new CompoundTag(StringUtils.EMPTY, nbt);
    }
}
