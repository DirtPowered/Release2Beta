package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.CollectPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityCollectItemPacket;
import com.github.steveice10.packetlib.Session;

public class CollectTranslator implements BetaToModern<CollectPacketData> {

    @Override
    public void translate(CollectPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        int targetEntityId = packet.getTargetEntityId();

        modernSession.send(new ServerEntityCollectItemPacket(entityId, targetEntityId, 1));
    }
}
