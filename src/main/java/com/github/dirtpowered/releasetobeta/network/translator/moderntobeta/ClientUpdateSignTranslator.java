package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.packet.data.UpdateSignPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientUpdateSignPacket;
import com.github.steveice10.packetlib.Session;

public class ClientUpdateSignTranslator implements ModernToBeta<ClientUpdateSignPacket> {

    @Override
    public void translate(ClientUpdateSignPacket packet, Session modernSession, BetaClientSession betaSession) {
        Position pos = packet.getPosition();
        String[] lines = packet.getLines();

        betaSession.sendPacket(new UpdateSignPacketData(pos.getX(), pos.getY(), pos.getZ(), lines));
    }
}
