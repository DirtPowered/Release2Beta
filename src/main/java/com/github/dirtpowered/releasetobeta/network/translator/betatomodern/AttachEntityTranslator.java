package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.AttachEntityPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityAttachPacket;
import com.github.steveice10.packetlib.Session;

public class AttachEntityTranslator implements BetaToModern<AttachEntityPacketData> {

    @Override
    public void translate(AttachEntityPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);

        int entityId = packet.getVehicleEntityId();
        int passenger = packet.getEntityId();

        modernSession.send(new ServerEntityAttachPacket(entityId, passenger));
    }
}
