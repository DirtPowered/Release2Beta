package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.HandshakePacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.LoginPacketData;
import com.github.dirtpowered.releasetobeta.data.ProtocolState;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.Session;

public class LoginStartTranslator implements ModernToBeta<LoginStartPacket> {

    @Override
    public void translate(LoginStartPacket packet, Session modernSession, BetaClientSession betaSession) {
        String username = packet.getUsername();
        if (betaSession.getProtocolState() != ProtocolState.LOGIN)
            return;

        betaSession.getPlayer().fillProfile(username, result -> {
            betaSession.sendPacket(new HandshakePacketData(username));
            betaSession.sendPacket(new LoginPacketData(14, username, 0, 0));
        });
    }
}
