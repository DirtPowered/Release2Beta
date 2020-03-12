package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.EntityDestroyPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.packetlib.Session;

public class EntityDestroyTranslator implements BetaToModern<EntityDestroyPacketData> {

    @Override
    public void translate(EntityDestroyPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();

        session.getMain().getEntityCache().removeEntity(entityId);
        modernSession.send(new ServerEntityDestroyPacket(entityId));
    }
}
