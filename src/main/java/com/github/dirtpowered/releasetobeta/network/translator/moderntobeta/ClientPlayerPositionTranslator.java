package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.packet.data.PlayerPositionPacketData;
import com.github.dirtpowered.releasetobeta.data.Constants;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerPositionTranslator implements ModernToBeta<ClientPlayerPositionPacket> {

    @Override
    public void translate(ClientPlayerPositionPacket packet, Session modernSession, BetaClientSession betaSession) {
        ModernPlayer player = betaSession.getPlayer();

        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        double stance = y + (player.isSneaking() ? Constants.PLAYER_STANCE_SNEAKING : Constants.PLAYER_STANCE);

        boolean onGround = packet.isOnGround();

        betaSession.sendPacket(new PlayerPositionPacketData(x, y, z, stance, onGround));
    }
}
