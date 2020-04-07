package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.KeepAlivePacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.packetlib.Session;

public class KeepAliveTranslator implements BetaToModern<KeepAlivePacketData> {

    @Override
    public void translate(KeepAlivePacketData packet, BetaClientSession session, Session modernSession) {
        modernSession.send(new ServerKeepAlivePacket(packet.getId()));
    }
}
