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
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.blockstorage.DataBlock;
import com.github.dirtpowered.releasetobeta.data.blockstorage.TempBlockStorage;
import com.github.dirtpowered.releasetobeta.data.chunk.BetaChunk;
import com.github.dirtpowered.releasetobeta.data.chunk.ModernChunk;
import com.github.dirtpowered.releasetobeta.data.entity.tile.TileEntity;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
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

    @Override
    public void translate(ReleaseToBeta main, MapChunkPacketData packet, BetaClientSession session, Session modernSession) {
        boolean skylight = session.getPlayer().getDimension() == 0;

        byte[] data = packet.getChunk();

        int x = packet.getX();
        int y = packet.getY();
        int z = packet.getZ();

        int chunkX = x / 16;
        int chunkZ = z / 16;

        try {
            if (y == 0) {
                BetaChunk chunk = new BetaChunk(chunkX, chunkZ, x, z);
                chunk.fillData(data, skylight);

                List<CompoundTag> chunkTileEntities = new LinkedList<>();
                NibbleArray3d[] skyLight = new NibbleArray3d[18];
                NibbleArray3d[] blockLight = new NibbleArray3d[18];

                ModernChunk[] modernChunks = new ModernChunk[16];
                Chunk[] chunks = new Chunk[16];

                for (int i = 0; i < 8; i++) {
                    modernChunks[i] = translateChunk(main, session, chunk, i * 16, skylight);
                    chunks[i] = modernChunks[i].getChunk();

                    chunkTileEntities.addAll(modernChunks[i].getChunkTileEntities());

                    blockLight[i + 1] = modernChunks[i].getBlockLight();
                    if (skylight) {
                        skyLight[i + 1] = modernChunks[i].getSkyLight();
                    }
                }

                modernSession.send(new ServerChunkDataPacket(
                        new Column(chunkX, chunkZ, chunks, chunkTileEntities.toArray(new CompoundTag[0]), new CompoundTag(StringUtil.EMPTY_STRING), Utils.getFilledBiomeData()))
                );

                modernSession.send(new ServerUpdateLightPacket(chunkX, chunkZ, skyLight, blockLight));
            } else if (R2BConfiguration.testMode) {
                //TODO: non-full chunks
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            main.getLogger().error("unable to convert chunk at x: " + chunkX + ", z: " + chunkZ);
        }
    }

    private ModernChunk translateChunk(ReleaseToBeta main, BetaClientSession session, BetaChunk chunk, int height, boolean skylight) {
        Chunk modernChunk = new Chunk();
        TempBlockStorage blockStorage = session.getBlockStorage();

        List<DataBlock> blockList = new ArrayList<>();
        List<CompoundTag> chunkTileEntities = new ArrayList<>();

        NibbleArray3d skyLight = new NibbleArray3d(4096);
        NibbleArray3d blockLight = new NibbleArray3d(4096);

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int legacyId = chunk.getTypeAt(x, y + height, z);
                    int legacyData = chunk.getMetadataAt(x, y + height, z);
                    int internalBlockId = main.getServer().convertBlockData(legacyId, legacyData, false);

                    if (skylight) {
                        if (legacyId == 54) {
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

                    if (blockStorage.needsCaching(legacyId)) {
                        blockList.add(new DataBlock(
                                new BlockLocation(chunk.getRawX() + x, y + height, chunk.getRawZ() + z),
                                new BlockLocation(x, y, z), legacyId)
                        );
                    }
                }
            }
        }

        blockStorage.cacheBlocks(chunk.getX(), chunk.getZ(), blockList.toArray(new DataBlock[0]));

        return new ModernChunk(
                main.getServer().getBlockConnector().connectBlocks(
                        blockStorage, chunk.getX(), chunk.getZ(), modernChunk
                ),
                chunkTileEntities,
                blockLight,
                skyLight
        );
    }
}
