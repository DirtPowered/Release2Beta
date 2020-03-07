package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.packet.data.AnimationPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.session.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerSwingArmTranslator implements ModernToBeta<ClientPlayerSwingArmPacket> {

    @Override
    public void translate(ClientPlayerSwingArmPacket packet, Session modernSession, BetaClientSession betaSession) {
        ModernPlayer player = betaSession.getPlayer();

        betaSession.sendPacket(new AnimationPacketData(player.getEntityId(), 1));
    }
}
