package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityMoveLookPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import com.github.steveice10.packetlib.Session;

public class EntityMoveLookTranslator implements BetaToModern<EntityMoveLookPacketData> {

    @Override
    public void translate(EntityMoveLookPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        double x = Utils.toModernPos(packet.getX());
        double y = Utils.toModernPos(packet.getY());
        double z = Utils.toModernPos(packet.getZ());

        float yaw = Utils.toModernYaw(packet.getYaw());
        float pitch = Utils.toModernPitch(packet.getPitch());

        modernSession.send(new ServerEntityPositionRotationPacket(entityId, x, y, z, yaw, pitch, true));
        modernSession.send(new ServerEntityHeadLookPacket(entityId, yaw));
    }
}