package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.EntityPositionPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.packetlib.Session;

public class EntityPositionTranslator implements BetaToModern<EntityPositionPacketData> {

    @Override
    public void translate(EntityPositionPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        int x = packet.getX();
        int y = packet.getY();
        int z = packet.getZ();
        //TODO: calculate movement

        //Logger.info("RelativePosition: X:{}, Y:{}, Z:{}", x, y, z);
        //modernSession.send(new ServerEntityPositionPacket(entityId, x, y, z, true));
    }
}
