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

package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.MapChunkPacketData;
import com.github.dirtpowered.betaprotocollib.utils.BlockLocation;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.Block;
import com.github.dirtpowered.releasetobeta.data.blockstorage.BlockDataFixer;
import com.github.dirtpowered.releasetobeta.data.blockstorage.ChunkCache;
import com.github.dirtpowered.releasetobeta.data.blockstorage.model.CachedBlock;
import com.github.dirtpowered.releasetobeta.data.chunk.BetaChunk;
import com.github.dirtpowered.releasetobeta.data.chunk.ModernChunk;
import com.github.dirtpowered.releasetobeta.data.entity.tile.TileEntity;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateLightPacket;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.packetlib.Session;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MapChunkTranslator implements BetaToModern<MapChunkPacketData> {

    private final static CompoundTag[] EMPTY_TAG_ARRAY;
    private final static int BS_ARRAY_SIZE;

    static {
        BS_ARRAY_SIZE = 18;
        EMPTY_TAG_ARRAY = new CompoundTag[0];
    }

    @Override
    public void translate(ReleaseToBeta main, MapChunkPacketData packet, BetaClientSession session, Session modernSession) {
        boolean skylight = session.getPlayer().getDimension() == 0;

        byte[] data = packet.getChunk();

        int rawX = packet.getX();
        int rawY = packet.getY();
        int rawZ = packet.getZ();

        int xSize = packet.getXSize();
        int ySize = packet.getYSize();
        int zSize = packet.getZSize();

        int chunkX = rawX / 16;
        int chunkZ = rawZ / 16;

        int chunkEdgeX = (rawX + xSize - 1) / 16;
        int chunkEdgeZ = (rawZ + zSize - 1) / 16;

        int height = rawY + ySize;

        boolean fullChunk = height == 128;

        try {
            for (int i = chunkX; i <= chunkEdgeX; ++i) {
                int x = Math.max(rawX - i * 16, 0);
                int newXSize = Math.min(rawX + xSize - i * 16, 16);

                for (int j = chunkZ; j <= chunkEdgeZ; ++j) {
                    int z = Math.max(rawZ - j * 16, 0);
                    int newZSize = Math.min(rawZ + zSize - j * 16, 16);

                    BetaChunk chunk;

                    if (session.getChunkCache().getChunk(chunkX, chunkZ) == null) {
                        chunk = new BetaChunk(chunkX, chunkZ, rawX, rawZ);
                    } else {
                        // get if cached
                        chunk = session.getChunkCache().getChunk(chunkX, chunkZ);
                    }

                    if (fullChunk) {
                        chunk.fillData(data, x, rawY, z, newXSize, height, newZSize, skylight);
                        session.getChunkCache().addChunk(chunkX, chunkZ, chunk);
                    } else {
                        // merge with full chunk data
                        chunk.fillData(data, x, rawY, z, newXSize, height, newZSize, skylight);
                    }

                    List<CompoundTag> chunkTileEntities = new LinkedList<>();

                    NibbleArray3d[] skyLight = new NibbleArray3d[BS_ARRAY_SIZE];
                    NibbleArray3d[] blockLight = new NibbleArray3d[BS_ARRAY_SIZE];

                    ModernChunk[] modernChunks = new ModernChunk[16];

                    Chunk[] chunks = new Chunk[16];

                    for (int k = 0; k < 8; k++) {
                        modernChunks[k] = translateChunk(main, session, chunk, k * 16, skylight);
                        chunks[k] = modernChunks[k].getChunk();

                        chunkTileEntities.addAll(modernChunks[k].getChunkTileEntities());

                        blockLight[k + 1] = modernChunks[k].getBlockLight();

                        if (skylight) {
                            skyLight[k + 1] = modernChunks[k].getSkyLight();
                        }
                    }

                    int[] biomeData = session.getOldChunkData().getBiomeDataAt(chunkX, chunkZ);

                    Column column = new Column(
                            chunkX, chunkZ, chunks,
                            chunkTileEntities.toArray(EMPTY_TAG_ARRAY),
                            new CompoundTag(StringUtil.EMPTY_STRING), fullChunk ? biomeData : null
                    );

                    modernSession.send(new ServerChunkDataPacket(column));
                    modernSession.send(new ServerUpdateLightPacket(chunkX, chunkZ, skyLight, blockLight));
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            main.getLogger().error("unable to convert chunk at x: " + chunkX + ", z: " + chunkZ);
        }
    }

    private ModernChunk translateChunk(ReleaseToBeta main, BetaClientSession session, BetaChunk chunk, int height, boolean skylight) {
        Chunk modernChunk = new Chunk();
        List<CompoundTag> chunkTileEntities = new ArrayList<>();

        NibbleArray3d skyLight = new NibbleArray3d(4096);
        NibbleArray3d blockLight = new NibbleArray3d(4096);

        ChunkCache chunkCache = session.getChunkCache();

        boolean dataFix = false;

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int legacyId = chunk.getTypeAt(x, y + height, z);
                    int legacyData = chunk.getMetadataAt(x, y + height, z);
                    int internalBlockId = main.getServer().convertBlockData(legacyId, legacyData, false);

                    if (skylight) {
                        if (legacyId == Block.CHEST) {
                            skyLight.set(x, y, z, 15); // fix chest lighting
                        } else {
                            skyLight.set(x, y, z, chunk.getSkyLightAt(x, y + height, z));
                        }
                    }

                    blockLight.set(x, y, z, chunk.getBlockLightAt(x, y + height, z));
                    modernChunk.set(x, y, z, new BlockState(internalBlockId));

                    if (TileEntity.containsId(legacyId)) {
                        TileEntity tile = TileEntity.create(legacyId);

                        if (tile != null) {
                            chunkTileEntities.add(tile.getNBT(new Position(chunk.getRawX() + x, y + height, chunk.getRawZ() + z)));
                        }
                    }

                    if (BlockDataFixer.canFix(legacyId)) dataFix = true;
                }
            }
        }

        if (dataFix) {
            for (CachedBlock block : BlockDataFixer.fixBlockData(chunkCache, chunk.getX(), chunk.getZ())) {
                BlockLocation loc = block.getBlockLocation();

                if ((loc.getY() >> 4 == height >> 4)) {
                    int chunkPosX = loc.getX() & 0xF;
                    int chunkPosY = loc.getY() & 0xF;
                    int chunkPosZ = loc.getZ() & 0xF;

                    modernChunk.set(chunkPosX, chunkPosY, chunkPosZ, new BlockState(main.getServer().convertBlockData(block.getTypeId(), block.getData(), false)));
                }
            }
        }

        return new ModernChunk(modernChunk, chunkTileEntities, blockLight, skyLight);
    }
}
