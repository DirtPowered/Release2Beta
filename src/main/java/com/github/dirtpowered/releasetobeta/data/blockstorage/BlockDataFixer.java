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

import com.github.dirtpowered.betaprotocollib.utils.BlockLocation;
import com.github.dirtpowered.releasetobeta.data.blockstorage.model.CachedBlock;

import java.util.ArrayList;
import java.util.List;

public class BlockDataFixer {

    private final static int OBSIDIAN = 49;
    private final static int PORTAL = 90;

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
        if (cachedBlock.getTypeId() == PORTAL) {
            BlockLocation loc = cachedBlock.getBlockLocation();

            int data = 0;

            if (worldTracker.getBlockAt(loc.getX() - 1, loc.getY(), loc.getZ()).getTypeId() == OBSIDIAN
                    || worldTracker.getBlockAt(loc.getX() + 1, loc.getY(), loc.getZ()).getTypeId() == OBSIDIAN) {
                data = 1;
            }

            if (worldTracker.getBlockAt(loc.getX(), loc.getY(), loc.getZ() - 1).getTypeId() == OBSIDIAN
                    || worldTracker.getBlockAt(loc.getX(), loc.getY(), loc.getZ() + 1).getTypeId() == OBSIDIAN) {
                data = 2;
            }

            return new CachedBlock(loc, PORTAL, data);
        }

        return null;
    }
}
