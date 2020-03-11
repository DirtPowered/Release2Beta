package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.packetlib.Session;
import org.pmw.tinylog.Logger;

public class ClientHandshakeTranslator implements ModernToBeta<HandshakePacket> {

    @Override
    public void translate(HandshakePacket packet, Session modernSession, BetaClientSession betaSession) {
        Logger.info("[{}] Received handshake with {} intent",
                betaSession.getClientId().toString().substring(0, 8), packet.getIntent());
    }
}
