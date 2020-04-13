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
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.chunk.BetaChunk;
import com.github.dirtpowered.releasetobeta.data.entity.TileEntity;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.packetlib.Session;
import org.pmw.tinylog.Logger;

public class MapChunkTranslator implements BetaToModern<MapChunkPacketData> {

    @Override
    public void translate(MapChunkPacketData packet, BetaClientSession session, Session modernSession) {
        boolean skylight = session.getPlayer().getDimension() == 0;

        byte[] data = packet.getChunk();

        int x = packet.getX();
        int y = packet.getY();
        int z = packet.getZ();

        int xSize = packet.getXSize();
        int ySize = packet.getYSize();
        int zSize = packet.getZSize();

        int chunkX = x / 16;
        int chunkZ = z / 16;

        try {
            if (y == 0) { //full chunks
                BetaChunk chunk = new BetaChunk(chunkX, chunkZ);

                chunk.fillData(data, skylight);
                Chunk[] chunks = new Chunk[16];
                for (int i = 0; i < 8; i++) { //8 chunks (max y = 128)
                    chunks[i] = translateChunk(session, chunk, i * 16, skylight);
                }

                modernSession.send(new ServerChunkDataPacket(new Column(chunkX, chunkZ, chunks, null)));
            } else if (R2BConfiguration.testMode) { //chunk structures (trees, block updates)
                /*
                 * In modern minecraft there's no way to send chunk like this, so we'll use BlockChange packet
                 * It still needs some work (metadata is missing and indexes are wrong sometimes)
                 * */
                for (int x1 = 0; x1 < xSize; x1++) {
                    for (int z1 = 0; z1 < zSize; z1++) {
                        for (int y1 = 0; y1 < ySize; y1++) {
                            int index = (x1 * xSize + z1) * ySize + y1;
                            int blockId = packet.getChunk()[index];

                            modernSession.send(new ServerBlockChangePacket(
                                    new BlockChangeRecord(new Position(x + x1, y + y1, z + z1), new BlockState(blockId, 0)))
                            );
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.warn("Chunk at [x={} z={}] was skipped", chunkX, chunkZ);
        }
    }

    private Chunk translateChunk(BetaClientSession session, BetaChunk chunk, int height, boolean skylight) {
        BlockStorage storage = new BlockStorage();
        NibbleArray3d nibbleBlockLight = new NibbleArray3d(4096);
        NibbleArray3d nibbleSkyLight = new NibbleArray3d(4096);

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int yh = y + height;

                    int blockId = session.remapBlock(chunk.getTypeAt(x, yh, z));
                    int blockData = session.remapMetadata(blockId, chunk.getMetadataAt(x, yh, z));
                    int blockLight = chunk.getBlockLightAt(x, yh, z);
                    int skyLight = chunk.getSkyLightAt(x, yh, z);

                    if (TileEntity.isTileEntity(blockId)) {
                        session.queueBlockChange(
                                Utils.fromChunkPos(chunk.getX()) + x, y + height,
                                Utils.fromChunkPos(chunk.getZ()) + z, blockId, blockData
                        );
                    }

                    storage.set(x, y, z, new BlockState(blockId, blockData));
                    nibbleBlockLight.set(x, y, z, blockLight);
                    nibbleSkyLight.set(x, y, z, skyLight);
                }
            }
        }

        return new Chunk(storage, nibbleBlockLight, skylight ? nibbleSkyLight : null);
    }
}
