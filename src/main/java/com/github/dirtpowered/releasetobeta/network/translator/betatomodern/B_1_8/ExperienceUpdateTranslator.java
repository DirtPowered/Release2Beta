package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.ExperienceUpdatePacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerSetExperiencePacket;
import com.github.steveice10.packetlib.Session;

public class ExperienceUpdateTranslator implements BetaToModern<ExperienceUpdatePacketData> {

    @Override
    public void translate(ExperienceUpdatePacketData packet, BetaClientSession session, Session modernSession) {
        int level = packet.getLevel();
        float progress = packet.getProgress(); //TODO: calculate progress
        int totalExperience = packet.getLevel();

        modernSession.send(new ServerPlayerSetExperiencePacket(0, level, totalExperience));
    }
}
