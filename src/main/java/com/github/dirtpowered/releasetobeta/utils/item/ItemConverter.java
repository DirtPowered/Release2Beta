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
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

public class ItemConverter {

    private final static int[] DAMAGEABLE_ITEMS = new int[] {
            256, 257, 258, 259, 261, 267, 268, 269, 270,
            271, 272, 273, 274, 275, 276, 277, 278, 279,
            283, 284, 285, 286, 290, 291, 292, 293, 294,
            298, 299, 300, 301, 302, 303, 304, 305, 306,
            307, 308, 309, 310, 311, 312, 313, 314, 315,
            316, 317, 346, 359
    };

    public static ItemStack betaToModern(ReleaseToBeta main, BetaItemStack item) {
        if (item == null)
            return new ItemStack(0);

        CompoundTag additionalTags;

        int internalItemId;
        if (isDamageable(item.getBlockId())) {
            internalItemId = main.getServer().convertBlockData(item.getBlockId(), 0, true);
            additionalTags = new CompoundTag("tag");
            additionalTags.put(new IntTag("Damage", item.getData()));

            additionalTags.put(removeItemAttributes());
        } else {
            internalItemId = main.getServer().convertBlockData(item.getBlockId(), item.getData(), true);
            CompoundTag attr = new CompoundTag(StringUtils.EMPTY);

            attr.put(removeItemAttributes());
            additionalTags = attr;
        }

        /*if (MinecraftVersion.B_1_9.isNewerOrEqual(R2BConfiguration.version) && item.hasNbt()) {
            //TODO: translate enchants to new format
            com.mojang.nbt.CompoundTag itemTag = item.getNbt();
            if (itemTag.contains("ench")) {
                com.mojang.nbt.ListTag listTag = itemTag.getList("ench");

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
        } else {*/
        return new ItemStack(internalItemId, item.getAmount(), additionalTags);
        //}
    }

    public static BetaItemStack itemStackToBetaItemStack(ItemStack itemStack) {
        //TODO: InternalId to LegacyId
        return new BetaItemStack(itemStack.getId(), itemStack.getAmount(), /*itemStack.getData()*/0);
    }

    public static ItemStack[] betaToModern(ReleaseToBeta main, BetaItemStack[] items) {
        ItemStack[] is = new ItemStack[items.length];

        for (int i = 0; i < items.length; i++) {
            BetaItemStack item = items[i];
            is[i] = betaToModern(main, item);
        }

        return is;
    }

    private static ListTag removeItemAttributes() {
        return new ListTag("AttributeModifiers", Collections.emptyList());
    }

    private static boolean isDamageable(int legacyId) {
        return ArrayUtils.contains(DAMAGEABLE_ITEMS, legacyId);
    }
}
