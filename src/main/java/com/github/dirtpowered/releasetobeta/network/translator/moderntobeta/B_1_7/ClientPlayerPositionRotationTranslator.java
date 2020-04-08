package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PlayerLookMovePacketData;
import com.github.dirtpowered.releasetobeta.data.Constants;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerPositionRotationTranslator implements ModernToBeta<ClientPlayerPositionRotationPacket> {

    @Override
    public void translate(ClientPlayerPositionRotationPacket packet, Session modernSession, BetaClientSession betaSession) {
        ModernPlayer player = betaSession.getPlayer();

        double x = packet.getX();
        double y = packet.getY() + (player.isSneaking() ? Constants.PLAYER_STANCE_SNEAKING : Constants.PLAYER_STANCE);
        double z = packet.getZ();
        double stance = packet.getY();

        float yaw = (float) packet.getYaw();
        float pitch = (float) packet.getPitch();

        boolean onGround = packet.isOnGround();

        betaSession.sendPacket(new PlayerLookMovePacketData(x, y, stance, z, yaw, pitch, onGround));
    }
}