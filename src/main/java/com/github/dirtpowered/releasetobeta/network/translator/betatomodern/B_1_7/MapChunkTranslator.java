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
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.blockstorage.DataBlock;
import com.github.dirtpowered.releasetobeta.data.chunk.BetaChunk;
import com.github.dirtpowered.releasetobeta.data.chunk.ModernChunk;
import com.github.dirtpowered.releasetobeta.data.entity.tile.TileEntity;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.packetlib.Session;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MapChunkTranslator implements BetaToModern<MapChunkPacketData> {

    private final static int COLUMN_ARRAY_SIZE = 16;

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

                ModernChunk[] modernChunks = new ModernChunk[COLUMN_ARRAY_SIZE];
                Chunk[] chunks = new Chunk[COLUMN_ARRAY_SIZE];

                for (int i = 0; i < 8; i++) {
                    modernChunks[i] = translateChunk(main, session, chunk, i * 16, skylight);
                    chunks[i] = modernChunks[i].getChunk();

                    chunkTileEntities.addAll(modernChunks[i].getChunkTileEntities());
                }

                modernSession.send(new ServerChunkDataPacket(new Column(chunkX, chunkZ, chunks, chunkTileEntities.toArray(new CompoundTag[0]), new CompoundTag("heightMaps"))));
            } else if (R2BConfiguration.testMode) {
                //TODO: non-full chunks
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            main.getLogger().error("unable to convert chunk at x: " + chunkX + ", z: " + chunkZ);
        }
    }

    private ModernChunk translateChunk(ReleaseToBeta main, BetaClientSession session, BetaChunk chunk, int height, boolean skylight) {
        Chunk modernChunk = new Chunk();
        List<DataBlock> blockList = new ArrayList<>();
        List<CompoundTag> chunkTileEntities = new ArrayList<>();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int legacyId = chunk.getTypeAt(x, y + height, z);
                    int legacyData = chunk.getMetadataAt(x, y + height, z);
                    int internalBlockId = main.getServer().convertBlockData(legacyId, legacyData, false);

                    modernChunk.set(x, y, z, new BlockState(internalBlockId));

                    if (TileEntity.containsId(legacyId)) {
                        TileEntity tile = TileEntity.create(legacyId);

                        if (tile != null) {
                            chunkTileEntities.add(tile.getNBT(new Position(chunk.getRawX() + x, y + height, chunk.getRawZ() + z)));
                        }
                    }

                    if (ArrayUtils.contains(session.getBlockStorage().getBlocksToCache(), legacyId)) {
                        blockList.add(new DataBlock(new Location(chunk.getRawX() + x, y + height, chunk.getRawZ() + z), new ImmutablePair<>(legacyId, 0)));
                    }
                }
            }
        }

        session.getBlockStorage().cacheBlocks(chunk.getX(), chunk.getZ(), blockList.toArray(new DataBlock[0]));
        return new ModernChunk(modernChunk, chunkTileEntities);
    }
}
