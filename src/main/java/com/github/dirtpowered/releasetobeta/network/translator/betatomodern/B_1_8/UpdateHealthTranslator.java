package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.UpdateHealthPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.packetlib.Session;

public class UpdateHealthTranslator implements BetaToModern<UpdateHealthPacketData> {

    @Override
    public void translate(UpdateHealthPacketData packet, BetaClientSession session, Session modernSession) {
        int health = packet.getHealth();
        int food = packet.getFood();
        float saturation = packet.getSaturation();

        modernSession.send(new ServerPlayerHealthPacket(health, food, saturation));
    }
}
