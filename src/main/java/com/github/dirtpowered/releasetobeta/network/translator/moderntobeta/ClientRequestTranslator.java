package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.packet.data.RespawnPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.packetlib.Session;

public class ClientRequestTranslator implements ModernToBeta<ClientRequestPacket> {

    @Override
    public void translate(ClientRequestPacket packet, Session modernSession, BetaClientSession betaSession) {
        ClientRequest request = packet.getRequest();

        if (request != ClientRequest.RESPAWN)
            return;

        betaSession.sendPacket(new RespawnPacketData((byte) betaSession.getPlayer().getDimension()));
    }
}
