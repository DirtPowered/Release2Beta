package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.HandshakePacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.packetlib.Session;
import org.pmw.tinylog.Logger;

public class HandshakeTranslator implements BetaToModern<HandshakePacketData> {

    @Override
    public void translate(HandshakePacketData packet, BetaClientSession session, Session modernSession) {
        Logger.info("received handshake. ClientId={}", session.getClientId());
    }
}
