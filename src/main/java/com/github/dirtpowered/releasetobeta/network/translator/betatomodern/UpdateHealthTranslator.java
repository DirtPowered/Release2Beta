package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.UpdateHealthPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.packetlib.Session;

public class UpdateHealthTranslator implements BetaToModern<UpdateHealthPacketData> {

    @Override
    public void translate(UpdateHealthPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        float health = packet.getHealth();

        modernSession.send(new ServerPlayerHealthPacket(health, 20, 0));
    }
}
