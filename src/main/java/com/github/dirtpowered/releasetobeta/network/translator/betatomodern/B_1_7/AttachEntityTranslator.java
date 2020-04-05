package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.AttachEntityPacketData;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityAttachPacket;
import com.github.steveice10.packetlib.Session;

public class AttachEntityTranslator implements BetaToModern<AttachEntityPacketData> {

    @Override
    public void translate(AttachEntityPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        ModernPlayer player = session.getPlayer();

        int entityId = packet.getVehicleEntityId();
        int passenger = packet.getEntityId();

        player.setInVehicle(entityId != -1);
        modernSession.send(new ServerEntityAttachPacket(entityId, passenger));
    }
}
