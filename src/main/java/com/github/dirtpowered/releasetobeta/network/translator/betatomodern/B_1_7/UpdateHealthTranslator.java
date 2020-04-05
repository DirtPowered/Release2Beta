package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.UpdateHealthPacketData;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.packetlib.Session;

public class UpdateHealthTranslator implements BetaToModern<UpdateHealthPacketData> {

    @Override
    public void translate(UpdateHealthPacketData packet, BetaClientSession session, Session modernSession) {
        float health = packet.getHealth();

        modernSession.send(new ServerPlayerHealthPacket(health, R2BConfiguration.disableSprinting ? 0 : 20, 0));
    }
}
