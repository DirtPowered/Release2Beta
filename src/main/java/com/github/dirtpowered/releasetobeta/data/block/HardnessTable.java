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
    BURNING_FURNACE(62, 3.5F, false, 278, 257, 274, 270, 285),
    DETECTOR_RAIL(28, 0.7F, false, -1),
    DISPENSER(23, 3.5F, false, 278, 257, 274, 270, 285),
    FENCE(85, 2.0F, false, -1),
    FURNACE(61, 3.5F, false, 278, 257, 274, 270, 285),
    GLOWING_REDSTONE_ORE(74, 3.0F, true, 278, 257, 274, 270, 285),
    JUKEBOX(84, 2.0F, false, -1),
    LADDER(65, 0.4F, false, -1),
    MOB_SPAWNER(52, 5.0F, false, 278, 257, 274, 270, 285),
    NOTE_BLOCK(25, 0.8F, false, -1),
    OBSIDIAN(49, 10.0F, false, 278),
    POWERED_RAIL(27, 0.7F, false, -1),
    RAIL(66, 0.7F, false, -1),
    REDSTONE_ORE(73, 3.0F, true, 278, 257, 274, 270, 285),
    STONE_PRESSURE_PLATE(70, 0.5F, false, 278, 257, 274, 270, 285),
    WOODEN_PRESSURE_PLATE(72, 0.5F, false, -1),
    WOODEN_TRAP_DOOR(96, 3.0F, false, -1),
    WOOD_STAIRS(53, 2.0F, false, -1),
    WORKBENCH(58, 2.5F, false, -1),
    IRON_DOOR_BLOCK(71, 5.0F, false, 278, 257, 274, 270, 285);

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
            if (toolId == 257) {
                multipler = 1.7F;
            } else if (toolId == 285) {
                multipler = 3.5F;
            } else if (toolId == 270) {
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
