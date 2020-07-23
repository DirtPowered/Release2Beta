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
import com.github.dirtpowered.releasetobeta.utils.Utils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class TempBlockStorage {

    @Getter
    private Long2ObjectMap<DataBlock[]> blockStorageMap = new Long2ObjectOpenHashMap<>();

    public boolean needsCaching(int legacyId) {
        return legacyId == 29 || legacyId == 33 || legacyId == 54 || legacyId == 78 || legacyId == 2;
    }

    public void cacheBlocks(int chunkX, int chunkZ, DataBlock[] blocks) {
        long hashKey = Utils.coordsToLong(chunkX, chunkZ);
        List<DataBlock> list = new ArrayList<>();

        for (DataBlock block : blocks) {
            list.add(block);
            blockStorageMap.put(hashKey, list.toArray(new DataBlock[0]));
        }
    }

    public void remove(int chunkX, int chunkZ) {
        blockStorageMap.remove(Utils.coordsToLong(chunkX, chunkZ));
    }

    public void purgeAll() {
        blockStorageMap.clear();
    }

    public DataBlock getCachedBlockAt(BlockLocation loc) {
        long hashKey = getChunkKey(loc);

        DataBlock block = new DataBlock(loc, new BlockLocation(0, 0, 0), 1);
        DataBlock[] blocks = blockStorageMap.get(hashKey);

        if (blocks != null) {
            for (DataBlock dataBlock : blocks) {

                if (dataBlock.getBlockLocation().equals(loc)) {
                    block = dataBlock;
                }
            }
        }

        return block;
    }

    private long getChunkKey(BlockLocation loc) {
        return Utils.coordsToLong((int) Math.floor(loc.getX()) >> 4, (int) Math.floor(loc.getZ()) >> 4);
    }
}
