package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.packet.data.HandshakePacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.LoginPacketData;
import com.github.dirtpowered.releasetobeta.data.ProtocolState;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.Session;
import org.pmw.tinylog.Logger;

public class LoginStartTranslator implements ModernToBeta<LoginStartPacket> {

    @Override
    public void translate(LoginStartPacket packet, Session modernSession, BetaClientSession betaSession) {
        String username = packet.getUsername();

        try {
            //TODO: send packets when server is ready
            Thread.sleep(300L);

            if (betaSession.getProtocolState() != ProtocolState.LOGIN)
                return;

            betaSession.sendPacket(new HandshakePacketData(username));
            betaSession.sendPacket(new LoginPacketData(14, username, 0, 0));

            betaSession.getPlayer().setUsername(username);
            betaSession.joinPlayer();
        } catch (InterruptedException e) {
            Logger.error("Error: {} ", e.getMessage());
        }
    }
}
