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
import com.github.dirtpowered.releasetobeta.data.Block;
import com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections.ChestConnection;
import com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections.FenceConnection;
import com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections.NetherPortalConnection;
import com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections.RedstoneConnection;
import com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections.SnowLayerConnection;
import com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections.model.BlockConnection;
import com.github.dirtpowered.releasetobeta.data.blockstorage.model.CachedBlock;
import com.github.dirtpowered.releasetobeta.data.mapping.flattening.DataConverter;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.packetlib.Session;

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
        register(Block.REDSTONE, new RedstoneConnection());
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

    private static int connectTo(ChunkCache chunkCache, BlockLocation loc, int block, int data) {
        int x = loc.getX();
        int y = loc.getY();
        int z = loc.getZ();

        boolean west = chunkCache.getBlockAt(x - 1, y, z) == block;
        boolean east = chunkCache.getBlockAt(x + 1, y, z) == block;
        boolean north = chunkCache.getBlockAt(x, y, z - 1) == block;
        boolean south = chunkCache.getBlockAt(x, y, z + 1) == block;

        boolean up = chunkCache.getBlockAt(x, y + 1, z) == block;
        boolean down = chunkCache.getBlockAt(x, y - 1, z) == block;

        return containsId(block) ? blockConnections.get(block).connect(west, east, north, south, up, down, data) : -1;
    }

    private static CachedBlock fixSingleBlockData(ChunkCache chunkCache, BlockLocation loc, int typeId, int typeData) {
        int x = loc.getX();
        int y = loc.getY();
        int z = loc.getZ();

        switch (typeId) {
            case Block.FENCE:
                return new CachedBlock(loc, Block.FENCE, connectTo(chunkCache, loc, Block.FENCE, 0));
            case Block.PORTAL:
                return new CachedBlock(loc, Block.PORTAL, connectTo(chunkCache, loc, Block.OBSIDIAN, 0));
            case Block.CHEST:
                return new CachedBlock(loc, Block.CHEST, connectTo(chunkCache, loc, Block.CHEST, typeData));
            case Block.REDSTONE:
                return new CachedBlock(loc, Block.REDSTONE, connectTo(chunkCache, loc, Block.REDSTONE, typeData));
        }

        // special case
        if (typeId == Block.SNOW_LAYER) {
            BlockLocation below = new BlockLocation(x, y - 1, z);

            if (chunkCache.getBlockAt(x, y - 1, z) == Block.GRASS_BLOCK) {
                return new CachedBlock(below, Block.GRASS_BLOCK, connectTo(chunkCache, below, Block.SNOW_LAYER, 0));
            }
        }

        return null;
    }

    public static List<CachedBlock> fixBlockData(ChunkCache chunkCache, int chunkX, int chunkZ) {
        List<CachedBlock> cachedBlocks = new ArrayList<>();

        for (CachedBlock cachedBlock : chunkCache.getCachedBlocksInChunk(chunkX, chunkZ)) {
            CachedBlock ready = fixSingleBlockData(chunkCache, cachedBlock.getBlockLocation(), cachedBlock.getTypeId(), cachedBlock.getData());
            if (ready != null) {
                cachedBlocks.add(ready);
            }
        }

        return cachedBlocks;
    }

    public static void updateNearby(Session session, ChunkCache cache, int posX, int posY, int posZ, boolean chunkChange) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                int newX = posX + x;
                int newZ = posZ + z;

                int typeId = cache.getBlockAt(newX, posY, newZ);
                int data = cache.getBlockDataAt(newX, posY, newZ);

                if (canFix(typeId) || typeId == Block.AIR && !chunkChange) {
                    if (typeId == Block.AIR) {
                        cache.onBlockUpdate(newX, posY, newZ, Block.AIR, 0);

                        sendBlockChangeAt(session, newX, posY, newZ, 0, 0);
                    }

                    CachedBlock b = fixSingleBlockData(cache, new BlockLocation(newX, posY, newZ), typeId, data);
                    if (b != null) {
                        cache.onBlockUpdate(newX, posY, newZ, b.getTypeId(), b.getData());

                        sendBlockChangeAt(session, newX, posY, newZ, b.getTypeId(), b.getData());
                    }
                }
            }
        }
    }

    private static void sendBlockChangeAt(Session session, int x, int y, int z, int typeId, int data) {
        session.send(new ServerBlockChangePacket(new BlockChangeRecord(
                new Position(x, y, z), new BlockState(DataConverter.getNewBlockId(typeId, data))
        )));
    }
}
