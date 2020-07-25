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

package com.github.dirtpowered.releasetobeta.data.blockstorage;

import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import com.github.dirtpowered.betaprotocollib.utils.BlockLocation;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.Block;
import com.github.dirtpowered.releasetobeta.data.blockstorage.model.CachedBlock;

import java.util.ArrayList;
import java.util.List;

public class BlockDataFixer {

    public static List<CachedBlock> fixBlockData(ClientWorldTracker worldTracker, int chunkX, int chunkZ) {
        List<CachedBlock> cachedBlocks = new ArrayList<>();

        for (CachedBlock cachedBlock : worldTracker.getCachedBlocksInChunk(chunkX, chunkZ)) {
            CachedBlock ready = fixSingleBlockData(worldTracker, cachedBlock);
            if (ready != null) {
                cachedBlocks.add(ready);
            }
        }

        return cachedBlocks;
    }

    public static CachedBlock fixSingleBlockData(ClientWorldTracker worldTracker, CachedBlock cachedBlock) {
        BlockLocation loc = cachedBlock.getBlockLocation();
        int typeId = cachedBlock.getTypeId();

        if (typeId == Block.PORTAL) {
            int data = 0;

            if (worldTracker.getBlockAt(loc.getX() - 1, loc.getY(), loc.getZ()).getTypeId() == Block.OBSIDIAN
                    || worldTracker.getBlockAt(loc.getX() + 1, loc.getY(), loc.getZ()).getTypeId() == Block.OBSIDIAN) {
                data = 1;
            }

            if (worldTracker.getBlockAt(loc.getX(), loc.getY(), loc.getZ() - 1).getTypeId() == Block.OBSIDIAN
                    || worldTracker.getBlockAt(loc.getX(), loc.getY(), loc.getZ() + 1).getTypeId() == Block.OBSIDIAN) {
                data = 2;
            }

            return new CachedBlock(loc, Block.PORTAL, data);

        } else if (typeId == Block.CHEST && !MinecraftVersion.B_1_8_1.isNewerOrEqual(R2BConfiguration.version)) {
            int data = 0;

            if (worldTracker.getBlockAt(loc.getX() - 1, loc.getY(), loc.getZ()).getTypeId() == Block.CHEST
                    || worldTracker.getBlockAt(loc.getX() + 1, loc.getY(), loc.getZ()).getTypeId() == Block.CHEST) {
                data = 3;
            }

            if (worldTracker.getBlockAt(loc.getX(), loc.getY(), loc.getZ() - 1).getTypeId() == Block.CHEST
                    || worldTracker.getBlockAt(loc.getX(), loc.getY(), loc.getZ() + 1).getTypeId() == Block.CHEST) {
                data = 4;
            }

            return new CachedBlock(loc, Block.CHEST, data);
        }

        return null;
    }

    public static boolean canFix(int legacyId) {
        return legacyId == Block.PORTAL || legacyId == Block.CHEST;
    }
}
