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

public enum ArmorItem {
    LEATHER_HELMET(298, 1.0D),
    LEATHER_CHESTPLATE(299, 3.0D),
    LEATHER_LEGGINGS(300, 2.0D),
    LEATHER_BOOTS(301, 1.0D),

    CHAINMAIL_HELMET(302, 2.0D),
    CHAINMAIL_CHESTPLATE(303, 5.0D),
    CHAINMAIL_LEGGINGS(304, 4.0D),
    CHAINMAIL_BOOTS(305, 1.0D),

    IRON_HELMET(306, 2.0D),
    IRON_CHESTPLATE(307, 6.0D),
    IRON_LEGGINGS(308, 5.0D),
    IRON_BOOTS(309, 2.0D),

    DIAMOND_HELMET(310, 3.0D),
    DIAMOND_CHESTPLATE(311, 8.0D),
    DIAMOND_LEGGINGS(312, 6.0D),
    DIAMOND_BOOTS(313, 3.0D),
    GOLDEN_HELMET(314, 2.0D),
    GOLDEN_CHESTPLATE(315, 5.0D),
    GOLDEN_LEGGINGS(316, 3.0D),
    GOLDEN_BOOTS(317, 1.0D);

    private int itemId;
    private double armorValue;

    ArmorItem(int itemId, double armorValue) {
        this.itemId = itemId;
        this.armorValue = armorValue;
    }

    public static double getArmorValueFromItemId(int itemId) {
        for (ArmorItem armorItem : values()) {
            if (itemId == armorItem.itemId) {
                return armorItem.armorValue;
            }
        }

        return 0.0D;
    }
}
