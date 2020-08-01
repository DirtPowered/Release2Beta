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

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.MultiBlockChangePacketData;
import com.github.dirtpowered.betaprotocollib.utils.BlockLocation;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.blockstorage.BlockDataFixer;
import com.github.dirtpowered.releasetobeta.data.blockstorage.model.CachedBlock;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import com.github.steveice10.packetlib.Session;

import java.util.LinkedList;
import java.util.List;

public class MultiBlockChangeTranslator implements BetaToModern<MultiBlockChangePacketData> {

    @Override
    public void translate(ReleaseToBeta main, MultiBlockChangePacketData packet, BetaClientSession session, Session modernSession) {
        int size = packet.getSize();
        short[] coordinateArray = packet.getCoordinateArray();
        byte[] blockArray = packet.getTypeArray();
        byte[] metadataArray = packet.getMetadataArray();

        int chunkX = packet.getX();
        int chunkZ = packet.getZ();

        boolean dataFix = false;

        List<BlockChangeRecord> records = new LinkedList<>();

        for (int index = 0; index < size; ++index) {
            short coord = coordinateArray[index];

            byte metadata = metadataArray[index];
            int block = session.remapBlock(blockArray[index] & 255, metadata, false);

            int blockX = (chunkX << 4) + (coord >> 12 & 15);
            int blockY = coord & 255;
            int blockZ = (chunkZ << 4) + (coord >> 8 & 15);

            records.add(new BlockChangeRecord(
                    new Position(blockX, blockY, blockZ), new BlockState(block, session.remapMetadata(block, metadata)))
            );

            if (BlockDataFixer.canFix(block)) dataFix = true;
        }

        for (BlockChangeRecord record : records) {
            Position pos = record.getPosition();

            session.getChunkCache().onBlockUpdate(pos.getX(), pos.getY(), pos.getZ(), record.getBlock().getId(), record.getBlock().getData());
        }

        if (dataFix) {
            for (CachedBlock block : BlockDataFixer.fixBlockData(session.getChunkCache(), chunkX, chunkZ)) {
                BlockLocation b = block.getBlockLocation();

                records.add(new BlockChangeRecord(
                        new Position(b.getX(), b.getY(), b.getZ()),
                        new BlockState(block.getTypeId(), block.getData()))
                );
            }
        }

        modernSession.send(new ServerMultiBlockChangePacket(records.toArray(new BlockChangeRecord[0])));
    }
}
