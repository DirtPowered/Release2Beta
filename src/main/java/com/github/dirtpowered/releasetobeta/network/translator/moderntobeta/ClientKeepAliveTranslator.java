package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.packet.data.KeepAlivePacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.packetlib.Session;

public class ClientKeepAliveTranslator implements ModernToBeta<ClientKeepAlivePacket> {

    @Override
    public void translate(ClientKeepAlivePacket packet, Session modernSession, BetaClientSession betaSession) {
        betaSession.sendPacket(new KeepAlivePacketData());
    }
}
