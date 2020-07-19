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

package com.github.dirtpowered.releasetobeta.data.item;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;

public enum ArmorItem {
    LEATHER_HELMET(563, 3.0D, 33),
    LEATHER_CHESTPLATE(564, 8.0D, 48),
    LEATHER_LEGGINGS(565, 6.0D, 45),
    LEATHER_BOOTS(566, 3.0D, 39),

    CHAINMAIL_HELMET(567, 3.0D, 66),
    CHAINMAIL_CHESTPLATE(568, 8.0D, 96),
    CHAINMAIL_LEGGINGS(569, 6.0D, 90),
    CHAINMAIL_BOOTS(570, 3.0D, 78),

    IRON_HELMET(571, 3.0D, 132),
    IRON_CHESTPLATE(572, 8.0D, 192),
    IRON_LEGGINGS(573, 6.0D, 180),
    IRON_BOOTS(574, 3.0D, 156),

    DIAMOND_HELMET(575, 3.0D, 264),
    DIAMOND_CHESTPLATE(576, 8.0D, 348),
    DIAMOND_LEGGINGS(577, 6.0D, 360),
    DIAMOND_BOOTS(578, 3.0D, 312),

    GOLDEN_HELMET(579, 3.0D, 66),
    GOLDEN_CHESTPLATE(580, 8.0D, 96),
    GOLDEN_LEGGINGS(581, 6.0D, 90),
    GOLDEN_BOOTS(582, 3.0D, 78);

    private int itemId;
    private int maxDurability;
    private double armorValue;

    ArmorItem(int itemId, double armorValue, int maxDurability) {
        this.itemId = itemId;
        this.armorValue = armorValue;
        this.maxDurability = maxDurability;
    }

    public static double getArmorValueFromItem(ItemStack itemStack) {
        for (ArmorItem armorItem : values()) {
            if (itemStack.getId() == armorItem.itemId) {
                CompoundTag rootTag = itemStack.getNbt();
                IntTag damageTag = rootTag.get("Damage");

                int damage = damageTag.getValue();
                return (armorItem.maxDurability - damage) * armorItem.armorValue / armorItem.maxDurability;
            }
        }

        return 0.0D;
    }
}
