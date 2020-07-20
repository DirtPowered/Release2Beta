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

import java.util.Arrays;

public enum ItemFood {
    APPLE(524),
    BREAD(562),
    PORK(584),
    GRILLED_PORK(585),
    GOLDEN_APPLE(587),
    RAW_FISH(625),
    COOKED_FISH(629),
    COOKIE(670),
    MELON(673),
    RAW_BEEF(677),
    COOKED_BEEF(678),
    RAW_CHICKEN(679),
    COOKED_CHICKEN(680),
    ROTTEN_FLESH(681);

    private int itemId;

    ItemFood(int itemId) {
        this.itemId = itemId;
    }

    public static boolean isFoodItem(int itemId) {
        return Arrays.stream(values()).anyMatch(itemFood -> itemId == itemFood.itemId);
    }
}
