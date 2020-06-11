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

public enum ArmorItem {
    LEATHER_HELMET(298, 3.0D, 33),
    LEATHER_CHESTPLATE(299, 8.0D, 48),
    LEATHER_LEGGINGS(300, 6.0D, 45),
    LEATHER_BOOTS(301, 3.0D, 39),

    CHAINMAIL_HELMET(302, 3.0D, 66),
    CHAINMAIL_CHESTPLATE(303, 8.0D, 96),
    CHAINMAIL_LEGGINGS(304, 6.0D, 90),
    CHAINMAIL_BOOTS(305, 3.0D, 78),

    IRON_HELMET(306, 3.0D, 132),
    IRON_CHESTPLATE(307, 8.0D, 192),
    IRON_LEGGINGS(308, 6.0D, 180),
    IRON_BOOTS(309, 3.0D, 156),

    DIAMOND_HELMET(310, 3.0D, 264),
    DIAMOND_CHESTPLATE(311, 8.0D, 348),
    DIAMOND_LEGGINGS(312, 6.0D, 360),
    DIAMOND_BOOTS(313, 3.0D, 312),

    GOLDEN_HELMET(314, 3.0D, 66),
    GOLDEN_CHESTPLATE(315, 8.0D, 96),
    GOLDEN_LEGGINGS(316, 6.0D, 90),
    GOLDEN_BOOTS(317, 3.0D, 78);

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
                return (armorItem.maxDurability - itemStack.getData()) * armorItem.armorValue / armorItem.maxDurability;
            }
        }

        return 0.0D;
    }
}
