package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityLookPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import com.github.steveice10.packetlib.Session;

public class EntityLookTranslator implements BetaToModern<EntityLookPacketData> {

    @Override
    public void translate(EntityLookPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        float yaw = Utils.toModernYaw(packet.getYaw());

        modernSession.send(new ServerEntityHeadLookPacket(entityId, yaw));
    }
}
