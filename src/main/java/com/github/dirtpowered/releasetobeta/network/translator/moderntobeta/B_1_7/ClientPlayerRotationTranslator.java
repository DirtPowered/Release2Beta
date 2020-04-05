package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PlayerLookPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerRotationTranslator implements ModernToBeta<ClientPlayerRotationPacket> {

    @Override
    public void translate(ClientPlayerRotationPacket packet, Session modernSession, BetaClientSession betaSession) {
        float yaw = (float) packet.getYaw();
        float pitch = (float) packet.getPitch();
        boolean onGround = packet.isOnGround();

        betaSession.sendPacket(new PlayerLookPacketData(yaw, pitch, onGround));
    }
}
