package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.EntityPositionPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import com.github.steveice10.packetlib.Session;
import org.pmw.tinylog.Logger;

public class EntityPositionTranslator implements BetaToModern<EntityPositionPacketData> {

    @Override
    public void translate(EntityPositionPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        double x = Utils.toModernPos(packet.getX());
        double y = Utils.toModernPos(packet.getY());
        double z = Utils.toModernPos(packet.getZ());

        Logger.info("RelativePosition: X:{}, Y:{}, Z:{}", x, y, z);
        modernSession.send(new ServerEntityPositionPacket(entityId, x, y, z, true));
    }
}
