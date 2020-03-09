package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.EntityMetadataPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.packetlib.Session;

public class EntityMetadataTranslator implements BetaToModern<EntityMetadataPacketData> {

    @Override
    public void translate(EntityMetadataPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        //EntityMetadataPacketData[entityId=7693,metadata=[WatchableObject [type=0, index=0, value=2]]] - START_SNEAK
        //EntityMetadataPacketData[entityId=7693,metadata=[WatchableObject [type=0, index=0, value=0]]] - STOP_SNEAK
        //TODO: we need more :]
    }
}
