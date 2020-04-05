package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.MultiBlockChangePacketData;
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
    public void translate(MultiBlockChangePacketData packet, BetaClientSession session, Session modernSession) {
        int size = packet.getSize();
        short[] coordinateArray = packet.getCoordinateArray();
        byte[] blockArray = packet.getTypeArray();
        byte[] metadataArray = packet.getMetadataArray();

        int chunkX = packet.getX();
        int chunkZ = packet.getZ();

        List<BlockChangeRecord> records = new LinkedList<>();
        for (int index = 0; index < size; ++index) {
            short coord = coordinateArray[index];

            int block = session.remapBlock(blockArray[index] & 255);
            byte metadata = metadataArray[index];

            int blockX = (chunkX << 4) + (coord >> 12 & 15);
            int blockY = coord & 255;
            int blockZ = (chunkZ << 4) + (coord >> 8 & 15);

            records.add(new BlockChangeRecord(
                    new Position(blockX, blockY, blockZ), new BlockState(block, metadata)));
        }

        modernSession.send(new ServerMultiBlockChangePacket(records.toArray(new BlockChangeRecord[0])));
    }
}
