package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.EntityMoveLookPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import com.github.steveice10.packetlib.Session;

public class EntityMoveLookTranslator implements BetaToModern<EntityMoveLookPacketData> {

    @Override
    public void translate(EntityMoveLookPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        int x = packet.getX();
        int y = packet.getY();
        int z = packet.getZ();
        //TODO: calculate movement

        float yaw = packet.getYaw();
        float pitch = packet.getPitch();

        modernSession.send(new ServerEntityPositionRotationPacket(entityId, 0, 0, 0, yaw, pitch, true));
    }
}
