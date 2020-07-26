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
import com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections.ChestConnection;
import com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections.FenceConnection;
import com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections.NetherPortalConnection;
import com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections.SnowLayerConnection;
import com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections.model.BlockConnection;
import com.github.dirtpowered.releasetobeta.data.blockstorage.model.CachedBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockDataFixer {
    private static final Map<Integer, BlockConnection> blockConnections = new HashMap<>();

    static {
        register(Block.FENCE, new FenceConnection());
        register(Block.OBSIDIAN, new NetherPortalConnection());
        register(Block.SNOW_LAYER, new SnowLayerConnection());
        register(Block.CHEST, new ChestConnection());
    }

    public static boolean canFix(int legacyId) {
        return legacyId == Block.PORTAL || legacyId == Block.SNOW_LAYER || legacyId == Block.FENCE || legacyId == Block.CHEST;
    }

    private static void register(int typeId, BlockConnection blockConnection) {
        blockConnections.put(typeId, blockConnection);
    }

    private static boolean containsId(int typeId) {
        return blockConnections.containsKey(typeId);
    }

    private static int connectTo(ClientWorldTracker worldTracker, BlockLocation loc, int block) {
        int x = loc.getX();
        int y = loc.getY();
        int z = loc.getZ();

        boolean west = worldTracker.getBlock(x - 1, y, z) == block;
        boolean east = worldTracker.getBlock(x + 1, y, z) == block;
        boolean north = worldTracker.getBlock(x, y, z - 1) == block;
        boolean south = worldTracker.getBlock(x, y, z + 1) == block;

        boolean up = worldTracker.getBlock(x, y + 1, z) == block;
        boolean down = worldTracker.getBlock(x, y - 1, z) == block;

        return containsId(block) ? blockConnections.get(block).connect(west, east, north, south, up, down) : -1;
    }

    public static CachedBlock fixSingleBlockData(ClientWorldTracker worldTracker, CachedBlock cachedBlock) {
        BlockLocation loc = cachedBlock.getBlockLocation();

        int typeId = cachedBlock.getTypeId();

        int x = loc.getX();
        int y = loc.getY();
        int z = loc.getZ();

        switch (typeId) {
            case Block.SNOW_LAYER:
                BlockLocation below = new BlockLocation(x, y - 1, z);
                return new CachedBlock(below, Block.GRASS_BLOCK, connectTo(worldTracker, below, Block.SNOW_LAYER));
            case Block.FENCE:
                return new CachedBlock(loc, Block.FENCE, connectTo(worldTracker, loc, Block.FENCE));
            case Block.PORTAL:
                return new CachedBlock(loc, Block.PORTAL, connectTo(worldTracker, loc, Block.OBSIDIAN));
        }

        // special case
        if (typeId == Block.CHEST && !MinecraftVersion.B_1_8_1.isNewerOrEqual(R2BConfiguration.version))
            return new CachedBlock(loc, Block.CHEST, connectTo(worldTracker, loc, Block.CHEST));

        return null;
    }

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
}
