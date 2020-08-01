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
import com.github.dirtpowered.releasetobeta.data.blockstorage.model.WorldTrackerImpl;
import com.github.dirtpowered.releasetobeta.data.chunk.BetaChunk;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChunkCache implements WorldTrackerImpl {

    private Long2ObjectMap<BetaChunk> chunks = new Long2ObjectOpenHashMap<>();

    private long getKey(int chunkX, int chunkZ) {
        return (long) chunkX & 0xffffffffL | ((long) chunkZ & 0xffffffffL) << 32;
    }

    public void addChunk(int chunkX, int chunkZ, BetaChunk chunk) {
        chunks.put(getKey(chunkX, chunkZ), chunk);
    }

    private void removeChunk(int chunkX, int chunkZ) {
        chunks.remove(getKey(chunkX, chunkZ));
    }

    public BetaChunk getChunk(int chunkX, int chunkZ) {
        return chunks.get(getKey(chunkX, chunkZ));
    }

    private void setBlockAndDataAt(int x, int y, int z, int legacyId, int data) {
        BetaChunk chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null)
            return;

        int chunkPosX = x & 0xF;
        int chunkPosY = y & 0x7F;
        int chunkPosZ = z & 0xF;

        chunk.setTypeAt(chunkPosX, chunkPosY, chunkPosZ, legacyId);
        chunk.setMetadataAt(chunkPosX, chunkPosY, chunkPosZ, data);
    }

    public int getBlockAt(int x, int y, int z) {
        BetaChunk chunk = getChunk(Utils.toChunkPos(x), Utils.toChunkPos(z));
        if (chunk == null)
            return 0;

        int chunkPosX = x & 0xF;
        int chunkPosY = y & 0x7F;
        int chunkPosZ = z & 0xF;

        return chunk.getTypeAt(chunkPosX, chunkPosY, chunkPosZ);
    }

    List<CachedBlock> getCachedBlocksInChunk(int chunkX, int chunkZ) {
        BetaChunk chunk = getChunk(chunkX, chunkZ);
        if (chunk == null)
            return Collections.emptyList();

        List<CachedBlock> cachedBlocks = new ArrayList<>();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 128; y++) {
                for (int z = 0; z < 16; z++) {
                    int typeId = chunk.getTypeAt(x, y, z);
                    int data = chunk.getMetadataAt(x, y, z);

                    int worldPositionX = (chunkX * 16) + x;
                    int worldPositionZ = (chunkZ * 16) + z;

                    cachedBlocks.add(new CachedBlock(new BlockLocation(worldPositionX, y, worldPositionZ), typeId, data));
                }
            }
        }

        return cachedBlocks;
    }

    @Override
    public void onBlockUpdate(int x, int y, int z, int typeId, int data) {
        setBlockAndDataAt(x, y, z, typeId, data);
    }

    @Override
    public void onChunkUnload(int chunkX, int chunkZ) {
        removeChunk(chunkX, chunkZ);
    }

    public void purge() {
        chunks.clear();
    }
}
