package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.BlockChangePacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.packetlib.Session;

public class BlockChangeTranslator implements BetaToModern<BlockChangePacketData> {

    @Override
    public void translate(BlockChangePacketData packet, BetaClientSession session, Session modernSession) {
        int x = packet.getXPosition();
        int y = packet.getYPosition();
        int z = packet.getZPosition();
        int blockId = session.remapBlock(packet.getType());
        int blockData = packet.getMetadata();

        modernSession.send(new ServerBlockChangePacket(new BlockChangeRecord(new Position(x, y, z), new BlockState(blockId, blockData))));
    }
}
