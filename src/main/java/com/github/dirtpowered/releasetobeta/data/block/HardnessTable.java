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

package com.github.dirtpowered.releasetobeta.data.block;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public enum HardnessTable {
    DETECTOR_RAIL(1322, 0.7F, false, -1),
    DISPENSER(244, 3.5F, false, 543, 521, 539, 535, 550),
    FENCE(3995, 2.0F, false, -1),
    GLOWING_REDSTONE_ORE(3883, 3.0F, true, 543, 521, 539, 535, 550),
    JUKEBOX(3963, 2.0F, false, -1),
    LADDER(3630, 0.4F, false, -1),
    MOB_SPAWNER(1951, 5.0F, false, 543, 521, 539, 535, 550),
    NOTE_BLOCK(249, 0.8F, false, -1),
    OBSIDIAN(1433, 10.0F, false, 543),
    POWERED_RAIL(1310, 0.7F, false, -1),
    RAIL(3643, 0.7F, false, -1),
    REDSTONE_ORE(3884, 3.0F, true, 543, 521, 539, 535, 550),
    STONE_PRESSURE_PLATE(3806, 0.5F, false, 543, 521, 539, 535, 550),
    WOODEN_PRESSURE_PLATE(3872, 0.5F, false, -1),
    WOODEN_TRAP_DOOR(4112, 3.0F, false, -1),
    WOOD_STAIRS(2023, 2.0F, false, -1),
    WORKBENCH(3354, 2.5F, false, -1);

    private int blockId;
    private float betaHardness;
    private int[] allowedTools;
    private boolean respectToolMultipler;

    HardnessTable(int blockId, float betaHardness, boolean respectToolMultipler, int... allowedTools) {
        this.blockId = blockId;
        this.betaHardness = betaHardness;
        this.respectToolMultipler = respectToolMultipler;
        this.allowedTools = allowedTools;
    }

    public static boolean exist(int blockId) {
        return Arrays.stream(values()).anyMatch(table -> blockId == table.blockId);
    }

    public static int getMiningTicks(int blockId, int toolId) {
        float multipler = 1.5F;

        HardnessTable block = getBlockById(blockId);
        boolean b = ArrayUtils.contains(block.allowedTools, toolId);

        if (!b && block.allowedTools[0] != -1) {
            multipler = 5.0F;
        }

        if (block.respectToolMultipler) {
            if (toolId == 521) {
                multipler = 1.7F;
            } else if (toolId == 550) {
                multipler = 3.5F;
            } else if (toolId == 535) {
                multipler = 5.0F;
            }
        }

        return (int) (getHardnessFromId(blockId) * multipler * 20);
    }

    public static float getHardnessFromId(int blockId) {
        for (HardnessTable table : values()) {
            if (table.blockId == blockId) {
                return table.betaHardness;
            }
        }

        return 0.f;
    }

    public static HardnessTable getBlockById(int blockId) {
        for (HardnessTable table : values()) {
            if (table.blockId == blockId) {
                return table;
            }
        }

        return values()[0];
    }
}