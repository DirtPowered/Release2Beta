package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityTeleportPacketData;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
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

        float yaw = Utils.toModernYaw(packet.getYaw());
        float pitch = Utils.toModernPitch(packet.getPitch());

        Entity e = session.getEntityCache().getEntityById(entityId);
        if (e != null) {
            e.setLocation(x, y, z);
        }

        modernSession.send(new ServerEntityTeleportPacket(entityId, x, y, z, yaw, pitch, true));
    }
}
