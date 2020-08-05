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
import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.packetlib.Session;

import java.util.ArrayList;
import java.util.List;

public class MapChunkTranslator implements BetaToModern<MapChunkPacketData> {

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

        int offsetX = (rawX + xSize - 1) / 16;
        int offsetZ = (rawZ + zSize - 1) / 16;

        int height = rawY + ySize;

        boolean fullChunk = rawY == 0;

        try {
            if (fullChunk) {
                BetaChunk chunk = new BetaChunk(chunkX, chunkZ, rawX, rawZ);

                chunk.fillData(data, skylight);
                session.getChunkCache().addChunk(chunkX, chunkZ, chunk);

                List<CompoundTag> chunkTileEntities = new ArrayList<>();
                ModernChunk[] chunks = new ModernChunk[16];
                Chunk[] _chunks = new Chunk[16];

                for (int k = 0; k < 8; k++) {
                    chunks[k] = translateChunk(session, chunk, k * 16, skylight);

                    chunkTileEntities.addAll(chunks[k].getChunkTileEntities());
                    _chunks[k] = chunks[k].getChunk();
                }

                modernSession.send(new ServerChunkDataPacket(new Column(chunkX, chunkZ, _chunks, chunkTileEntities.toArray(new CompoundTag[0]))));
            } else {
                for (int i = chunkX; i <= offsetX; ++i) {
                    int x = Math.max(rawX - i * 16, 0);
                    int newXSize = Math.min(rawX + xSize - i * 16, 16);

                    for (int j = chunkZ; j <= offsetZ; ++j) {
                        int z = Math.max(rawZ - j * 16, 0);
                        int newZSize = Math.min(rawZ + zSize - j * 16, 16);

                        BetaChunk testChunk = new BetaChunk(chunkX, chunkZ, rawX, rawZ);
                        testChunk.fillData(data, x, rawY, z, newXSize, height, newZSize, skylight, false);

                        List<BlockChangeRecord> blockChangeRecords = new ArrayList<>();

                        if (rawX >= 0 && rawZ >= 0) {
                            for (int x1 = 0; x1 < (newXSize < 0 ? 16 : newXSize); x1++) {
                                for (int y1 = 0; y1 < height; y1++) {
                                    for (int z1 = 0; z1 < (newZSize < 0 ? 16 : newZSize); z1++) {
                                        int blockId = testChunk.getTypeAt(x1, y1, z1);
                                        if (blockId != -1) {
                                            int blockData = testChunk.getMetadataAt(x1, y1, z1);

                                            blockChangeRecords.add(new BlockChangeRecord(
                                                    new Position(rawX + (x1 - x), y1, rawZ + (z1 - z)), new BlockState(blockId, blockData)
                                            ));
                                        }
                                    }
                                }
                            }
                        } else {
                            // negative X, Z workaround
                            int newIndex = 0;

                            if (data.length >= 8) {
                                for (int x1 = 0; x1 < xSize; x1++) {
                                    for (int z1 = 0; z1 < zSize; z1++) {
                                        for (int y1 = 0; y1 < ySize; y1++) {
                                            int blockId = data[newIndex];

                                            //TODO: get block data index
                                            blockChangeRecords.add(new BlockChangeRecord(
                                                    new Position(rawX + x1, rawY + y1, rawZ + z1), new BlockState(blockId, 0)
                                            ));

                                            newIndex++;
                                        }
                                    }
                                }
                            }
                        }

                        if (!blockChangeRecords.isEmpty()) {
                            modernSession.send(new ServerMultiBlockChangePacket(blockChangeRecords.toArray(new BlockChangeRecord[0])));
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            main.getLogger().error("unable to convert chunk at x: " + chunkX + ", z: " + chunkZ);
        }
    }

    private ModernChunk translateChunk(BetaClientSession session, BetaChunk chunk, int height, boolean skylight) {
        BlockStorage storage = new BlockStorage();
        NibbleArray3d nibbleBlockLight = new NibbleArray3d(4096);
        NibbleArray3d nibbleSkyLight = new NibbleArray3d(4096);

        ChunkCache chunkCache = session.getChunkCache();

        boolean dataFix = false;

        List<CompoundTag> chunkTileEntities = new ArrayList<>();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    if (chunk.getTypeAt(x, y + height, z) != -1) {
                        int data = chunk.getMetadataAt(x, y + height, z);

                        int blockId = session.remapBlock(chunk.getTypeAt(x, y + height, z), data, false);
                        int blockData = session.remapMetadata(blockId, data);

                        storage.set(x, y, z, new BlockState(blockId, blockData));

                        if (skylight) {
                            if (blockId == Block.CHEST) {
                                nibbleSkyLight.set(x, y, z, 15); // fix chest lighting
                            } else {
                                nibbleSkyLight.set(x, y, z, chunk.getSkyLightAt(x, y + height, z));
                            }
                        }

                        nibbleBlockLight.set(x, y, z, chunk.getBlockLightAt(x, y + height, z));

                        if (TileEntity.containsId(blockId)) {
                            TileEntity tile = TileEntity.create(blockId);

                            if (tile != null) {
                                chunkTileEntities.add(tile.getNBT(new Position(chunk.getRawX() + x, y + height, chunk.getRawZ() + z)));
                            }
                        }

                        if (BlockDataFixer.canFix(blockId)) dataFix = true;
                    }
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

                    storage.set(chunkPosX, chunkPosY, chunkPosZ, new BlockState(block.getTypeId(), block.getData()));
                }
            }
        }

        return new ModernChunk(new Chunk(storage, nibbleBlockLight, skylight ? nibbleSkyLight : null), chunkTileEntities);
    }
}
