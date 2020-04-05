package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.SleepPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerUseBedPacket;
import com.github.steveice10.packetlib.Session;

public class SleepPacketTranslator implements BetaToModern<SleepPacketData> {

    @Override
    public void translate(SleepPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        int x = packet.getX();
        int y = packet.getY();
        int z = packet.getZ();

        Position pos = new Position(x, y, z);

        modernSession.send(new ServerPlayerUseBedPacket(entityId, pos));
    }
}
