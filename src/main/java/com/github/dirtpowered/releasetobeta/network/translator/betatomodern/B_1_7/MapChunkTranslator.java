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
import com.github.dirtpowered.betaprotocollib.utils.Location;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.blockstorage.DataBlock;
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
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.packetlib.Session;

import java.util.ArrayList;
import java.util.List;

public class MapChunkTranslator implements BetaToModern<MapChunkPacketData> {

    @Override
    public void translate(MapChunkPacketData packet, BetaClientSession session, Session modernSession) {
        boolean skylight = session.getPlayer().getDimension() == 0;

        byte[] data = packet.getChunk();

        int x = packet.getX();
        int y = packet.getY();
        int z = packet.getZ();

        int chunkX = x / 16;
        int chunkZ = z / 16;

        try {
            if (y == 0) { //full chunks
                BetaChunk chunk = new BetaChunk(chunkX, chunkZ, x, z);
                List<CompoundTag> chunkTileEntities = new ArrayList<>();

                chunk.fillData(data, skylight);
                ModernChunk[] chunks = new ModernChunk[16];
                Chunk[] _chunks = new Chunk[16];
                for (int i = 0; i < 8; i++) {
                    chunks[i] = translateChunk(session, chunk, i * 16, skylight);

                    chunkTileEntities.addAll(chunks[i].getChunkTileEntities());
                    _chunks[i] = chunks[i].getChunk();
                }
                modernSession.send(new ServerChunkDataPacket(new Column(chunkX, chunkZ, _chunks, chunkTileEntities.toArray(new CompoundTag[0]))));
            } else if (R2BConfiguration.testMode) {
                //TODO: non-full chunks
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            session.getMain().getLogger().warning("Chunk at [x=" + chunkX + " z=" + chunkZ + "] was skipped");
        }
    }

    private ModernChunk translateChunk(BetaClientSession session, BetaChunk chunk, int height, boolean skylight) {
        BlockStorage storage = new BlockStorage();
        NibbleArray3d nibbleBlockLight = new NibbleArray3d(4096);
        NibbleArray3d nibbleSkyLight = new NibbleArray3d(4096);

        List<DataBlock> blockList = new ArrayList<>();
        List<CompoundTag> chunkTileEntities = new ArrayList<>();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int worldXPos = chunk.getRawX() + x;
                    int worldYPos = y + height;
                    int worldZPos = chunk.getRawZ() + z;

                    int data = chunk.getMetadataAt(x, worldYPos, z);

                    int blockId = session.remapBlock(chunk.getTypeAt(x, worldYPos, z), data, false);
                    int blockData = session.remapMetadata(blockId, data);

                    BlockState blockState = new BlockState(blockId, blockData);

                    blockList.add(new DataBlock(new Location(worldXPos, worldYPos, worldZPos), blockState));

                    storage.set(x, y, z, blockState);
                    nibbleBlockLight.set(x, y, z, chunk.getBlockLightAt(x, worldYPos, z));
                    nibbleSkyLight.set(x, y, z, chunk.getSkyLightAt(x, worldYPos, z));


                    if (TileEntity.containsId(blockId))
                        chunkTileEntities.add(TileEntity.create(blockId).getNBT(new Position(worldXPos, worldYPos, worldZPos)));
                }
            }
        }

        session.getBlockStorage().cacheBlocks(chunk.getX(), chunk.getZ(), blockList.toArray(new DataBlock[0]));
        return new ModernChunk(new Chunk(storage, nibbleBlockLight, skylight ? nibbleSkyLight : null), chunkTileEntities);
    }
}
