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

import com.github.dirtpowered.betaprotocollib.utils.Location;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TempBlockStorage {

    @Getter
    private Map<Long, DataBlock[]> blockStorageMap = new HashMap<>();
    private int[] blocksToCache = new int[]{29, 33, 54};

    public void cacheBlocks(int chunkX, int chunkZ, DataBlock[] blocks) {
        if (needsCaching(blocks)) {
            long hashKey = coordsToLong(chunkX, chunkZ);

            blockStorageMap.put(hashKey, blocks);
        }
    }

    private boolean needsCaching(DataBlock[] blocks) {
        return Arrays.stream(blocks).anyMatch(block -> Arrays.stream(blocksToCache).anyMatch(i -> block.getBlockState().getId() == i));
    }

    private long coordsToLong(int x, int z) {
        int chunkX = (int) Math.floor(x) << 4;
        int chunkZ = (int) Math.floor(z) << 4;
        return (long) chunkX & 0xffffffffL | ((long) chunkZ & 0xffffffffL) << 32;
    }

    public void remove(int chunkX, int chunkZ) {
        blockStorageMap.remove(coordsToLong(chunkX, chunkZ));
    }

    public void purgeAll() {
        blockStorageMap.clear();
    }

    public DataBlock getCachedBlockAt(Location loc) {
        long hashKey = getChunkKey(loc);

        DataBlock block = new DataBlock(loc, new BlockState(1, 0));
        DataBlock[] blocks = blockStorageMap.get(hashKey);

        if (blocks != null) {
            for (DataBlock dataBlock : blocks) {

                if (dataBlock.getLocation().equals(loc)) {
                    block = dataBlock;
                }
            }
        }

        return block;
    }

    private long getChunkKey(Location loc) {
        return coordsToLong((int) Math.floor(loc.getX()) >> 4, (int) Math.floor(loc.getZ()) >> 4);
    }
}
