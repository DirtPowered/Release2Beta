package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.ExperienceOrbPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnExpOrbPacket;
import com.github.steveice10.packetlib.Session;

public class ExperienceOrbTranslator implements BetaToModern<ExperienceOrbPacketData> {

    @Override
    public void translate(ExperienceOrbPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        int x = (int) Utils.toModernPos(packet.getX());
        int y = (int) Utils.toModernPos(packet.getY());
        int z = (int) Utils.toModernPos(packet.getZ());

        int count = packet.getCount();

        modernSession.send(new ServerSpawnExpOrbPacket(entityId, x, y, z, count));
    }
}
