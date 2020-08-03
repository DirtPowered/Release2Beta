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

    public static List<CachedBlock> fixBlockData(ChunkCache chunkCache, int chunkX, int chunkZ) {
        List<CachedBlock> cachedBlocks = new ArrayList<>();

        for (CachedBlock cachedBlock : chunkCache.getCachedBlocksInChunk(chunkX, chunkZ)) {
            CachedBlock ready = fixSingleBlockData(chunkCache, cachedBlock);
            if (ready != null) {
                cachedBlocks.add(ready);
            }
        }

        return cachedBlocks;
    }

    public static CachedBlock fixSingleBlockData(ChunkCache chunkCache, CachedBlock cachedBlock) {
        BlockLocation loc = cachedBlock.getBlockLocation();
        int typeId = cachedBlock.getTypeId();

        if (typeId == Block.PORTAL) {
            int data = 0;

            if (chunkCache.getBlockAt(loc.getX() - 1, loc.getY(), loc.getZ()) == Block.OBSIDIAN
                    || chunkCache.getBlockAt(loc.getX() + 1, loc.getY(), loc.getZ()) == Block.OBSIDIAN) {
                data = 1;
            }

            if (chunkCache.getBlockAt(loc.getX(), loc.getY(), loc.getZ() - 1) == Block.OBSIDIAN
                    || chunkCache.getBlockAt(loc.getX(), loc.getY(), loc.getZ() + 1) == Block.OBSIDIAN) {
                data = 2;
            }

            return new CachedBlock(loc, Block.PORTAL, data);

        } else if (typeId == Block.CHEST && !MinecraftVersion.B_1_8_1.isNewerOrEqual(R2BConfiguration.version)) {
            int data = 2;
            boolean doubleChest = false;

            // double chests
            if (chunkCache.getBlockAt(loc.getX() - 1, loc.getY(), loc.getZ()) == Block.CHEST
                    || chunkCache.getBlockAt(loc.getX() + 1, loc.getY(), loc.getZ()) == Block.CHEST) {
                data = 3;
                doubleChest = true;
            }

            if (chunkCache.getBlockAt(loc.getX(), loc.getY(), loc.getZ() - 1) == Block.CHEST
                    || chunkCache.getBlockAt(loc.getX(), loc.getY(), loc.getZ() + 1) == Block.CHEST) {
                data = 4;
                doubleChest = true;
            }

            // single chests
            if (!doubleChest) {
                if (isSolid(chunkCache.getBlockAt(loc.getX() - 1, loc.getY(), loc.getZ()))) {
                    data = 5;
                }

                if (isSolid(chunkCache.getBlockAt(loc.getX() + 1, loc.getY(), loc.getZ()))) {
                    data = 4;
                }

                if (isSolid(chunkCache.getBlockAt(loc.getX(), loc.getY(), loc.getZ() - 1))) {
                    data = 3;
                }

                if (isSolid(chunkCache.getBlockAt(loc.getX(), loc.getY(), loc.getZ() + 1))) {
                    data = 2;
                }
            }

            return new CachedBlock(loc, Block.CHEST, data);
        }

        return null;
    }

    public static boolean canFix(int legacyId) {
        return legacyId == Block.PORTAL || legacyId == Block.CHEST;
    }

    private static boolean isSolid(int blockId) {
        return blockId != 0 && blockId != 54; //TODO: better check
    }
}
