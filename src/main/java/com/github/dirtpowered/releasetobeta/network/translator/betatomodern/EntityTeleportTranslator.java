package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.EntityTeleportPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.packetlib.Session;

public class EntityTeleportTranslator implements BetaToModern<EntityTeleportPacketData> {

    @Override
    public void translate(EntityTeleportPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        double x = Utils.toModernPos(packet.getX());
        double y = Utils.toModernPos(packet.getY());
        double z = Utils.toModernPos(packet.getZ());

        float yaw = packet.getYaw();
        float pitch = packet.getPitch();

        //Logger.info("TeleportPosition({}): X:{}, Y:{}, Z:{}", entityId, x, y, z);
        modernSession.send(new ServerEntityTeleportPacket(entityId, x, y, z, yaw, pitch, true));
    }
}
