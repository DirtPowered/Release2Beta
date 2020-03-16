package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.packet.data.UseEntityPacketData;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerInteractEntityTranslator implements ModernToBeta<ClientPlayerInteractEntityPacket> {

    @Override
    public void translate(ClientPlayerInteractEntityPacket packet, Session modernSession, BetaClientSession betaSession) {
        ModernPlayer player = betaSession.getPlayer();
        int targetEntityId = packet.getEntityId();
        int entityId = player.getEntityId();
        InteractAction action = packet.getAction();

        betaSession.sendPacket(new UseEntityPacketData(entityId, targetEntityId, action == InteractAction.ATTACK));
    }
}
