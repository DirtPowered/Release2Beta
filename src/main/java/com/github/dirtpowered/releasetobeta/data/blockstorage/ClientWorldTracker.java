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
import com.github.dirtpowered.releasetobeta.utils.Utils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import jdk.nashorn.internal.objects.annotations.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientWorldTracker implements WorldTrackerImpl {

    private Long2ObjectMap<Set<CachedBlock>> worldTrackerStorage = new Long2ObjectOpenHashMap<>();

    @Getter
    public boolean needsCaching(int legacyId) {
        return legacyId == 29 || legacyId == 33 || legacyId == 54 || legacyId == 90 || legacyId == 49;
    }

    @Override
    public void onBlockUpdate(BlockLocation blockLocation, int typeId, int data) {
        if (!needsCaching(typeId) && typeId != 0)
            return;

        int chunkX = Utils.toChunkPos(blockLocation.getX());
        int chunkZ = Utils.toChunkPos(blockLocation.getZ());

        long key = Utils.coordsToLong(chunkX, chunkZ);
        CachedBlock cachedBlock = new CachedBlock(blockLocation, typeId, data);

        if (worldTrackerStorage.containsKey(key)) {
            Set<CachedBlock> blocks = worldTrackerStorage.get(key);
            if (typeId == 0) {
                blocks.remove(cachedBlock);
            } else {
                blocks.add(cachedBlock);
            }
        } else {
            worldTrackerStorage.put(key, new HashSet<>());
        }
    }

    @Override
    public void onMultiBlockUpdate(List<CachedBlock> cachedBlocks) {
        for (CachedBlock block : cachedBlocks) {
            int chunkX = Utils.toChunkPos(block.getBlockLocation().getX());
            int chunkZ = Utils.toChunkPos(block.getBlockLocation().getZ());

            long key = Utils.coordsToLong(chunkX, chunkZ);

            if (worldTrackerStorage.containsKey(key)) {
                Set<CachedBlock> blocks = worldTrackerStorage.get(key);
                for (CachedBlock cachedBlock : cachedBlocks) {
                    int typeId = cachedBlock.getTypeId();

                    if (needsCaching(typeId) && typeId != 0) {
                        if (cachedBlock.getTypeId() == 0) {
                            blocks.remove(cachedBlock);
                        } else {
                            blocks.add(cachedBlock);
                        }
                    }
                }
            } else {
                worldTrackerStorage.put(key, new HashSet<>());
            }
        }
    }

    CachedBlock getBlockAt(int x, int y, int z) {
        return getBlockAt(new BlockLocation(x, y, z));
    }

    public CachedBlock getBlockAt(BlockLocation blockLocation) {
        int chunkX = Utils.toChunkPos(blockLocation.getX());
        int chunkZ = Utils.toChunkPos(blockLocation.getZ());

        long key = Utils.coordsToLong(chunkX, chunkZ);

        if (worldTrackerStorage.containsKey(key)) {
            Set<CachedBlock> blocks = worldTrackerStorage.get(key);
            for (CachedBlock entry : blocks) {
                if (blockLocation.equals(entry.getBlockLocation())) {
                    return entry;
                }
            }
        }

        return new CachedBlock(blockLocation, 0, 0);
    }

    @Override
    public void onChunkBlockUpdate(int chunkX, int chunkZ, List<CachedBlock> cachedBlocks) {
        if (cachedBlocks.isEmpty())
            return;

        long key = Utils.coordsToLong(chunkX, chunkZ);

        if (worldTrackerStorage.containsKey(key)) {
            Set<CachedBlock> blocks = worldTrackerStorage.get(key);
            blocks.addAll(cachedBlocks);
        } else {
            worldTrackerStorage.put(key, new HashSet<>(cachedBlocks));
        }
    }

    @Override
    public void onChunkUnload(int chunkX, int chunkZ) {
        worldTrackerStorage.remove(Utils.coordsToLong(chunkX, chunkZ));
    }

    Set<CachedBlock> getCachedBlocksInChunk(int chunkX, int chunkZ) {
        long key = Utils.coordsToLong(chunkX, chunkZ);

        return worldTrackerStorage.containsKey(key) ? worldTrackerStorage.get(key) : Collections.emptySet();
    }
}
