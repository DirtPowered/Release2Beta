package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PlayerLookMovePacketData;
import com.github.dirtpowered.betaprotocollib.utils.Location;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.Session;

public class PlayerLookMoveTranslator implements BetaToModern<PlayerLookMovePacketData> {

    @Override
    public void translate(PlayerLookMovePacketData packet, BetaClientSession session, Session modernSession) {
        ModernPlayer player = session.getPlayer();

        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();

        float yaw = packet.getYaw();
        float pitch = packet.getPitch();

        double stance = y - packet.getStance();

        player.setLocation(new Location(x, y, z, yaw, pitch)); //server-side location
        modernSession.send(new ServerPlayerPositionRotationPacket(x, y - stance, z, yaw, pitch, 0));
    }
}
