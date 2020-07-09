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

package com.github.dirtpowered.releasetobeta.data.achievement;

import lombok.Getter;

@Getter
public enum Achievement {
    // 1.6.6-1.7.3
    OPEN_INVENTORY(0),
    MINE_WOOD(1),
    BUILD_WORKBENCH(2),
    BUILD_PICKAXE(3),
    BUILD_FURNACE(4),
    ACQUIRE_IRON(5),
    BUILD_HOE(6),
    MAKE_BREAD(7),
    BAKE_CAKE(8),
    BUILD_BETTER_PICKAXE(9),
    COOK_FISH(10),
    ON_A_RAIL(11),
    BUILD_SWORD(12),
    KILL_ENEMY(13),
    KILL_COW(14),
    FLY_PIG(15),
    // 1.8-1.9pre6
    SNIPE_SKELETON(16),
    DIAMONDS(17),
    PORTAL(18),
    GHAST(19),
    BLAZ_EROD(20),
    POTION(21),
    THE_END(22),
    THE_END_2(23),
    ENCHANTMENTS(24),
    OVER_KILL(25),
    BOOKCASE(26);

    public static final int ACHIEVEMENT_OFFSET = 5242880;
    private int statId;

    Achievement(int statId) {
        this.statId = statId;
    }

    public static Achievement fromStatId(int statId) {
        for (Achievement achievement : values()) {
            if (statId == achievement.statId) {
                return achievement;
            }
        }

        return Achievement.OPEN_INVENTORY;
    }
}