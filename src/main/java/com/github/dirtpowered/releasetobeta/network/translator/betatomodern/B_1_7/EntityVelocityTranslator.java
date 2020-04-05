package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityVelocityPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import com.github.steveice10.packetlib.Session;

public class EntityVelocityTranslator implements BetaToModern<EntityVelocityPacketData> {

    @Override
    public void translate(EntityVelocityPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();

        double moveX = packet.getMotionX() / 8000.0D;
        double moveY = packet.getMotionY() / 8000.0D;
        double moveZ = packet.getMotionZ() / 8000.0D;

        modernSession.send(new ServerEntityVelocityPacket(entityId, moveX, moveY, moveZ));
    }
}
