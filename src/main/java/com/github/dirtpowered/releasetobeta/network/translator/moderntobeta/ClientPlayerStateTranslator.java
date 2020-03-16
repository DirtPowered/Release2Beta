package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.packet.data.EntityActionPacketData;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerState;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerStateTranslator implements ModernToBeta<ClientPlayerStatePacket> {

    @Override
    public void translate(ClientPlayerStatePacket packet, Session modernSession, BetaClientSession betaSession) {
        int entityId = packet.getEntityId();
        PlayerState state = packet.getState();
        ModernPlayer player = betaSession.getPlayer();
        int newState;

        switch (state) {
            case START_SNEAKING:
                player.setSneaking(true);

                newState = 1;
                break;
            case STOP_SNEAKING:
                player.setSneaking(false);

                newState = 2;
                break;
            case LEAVE_BED:
                newState = 3;
                break;
            default:
                newState = -1;
                break;
        }

        if (newState == -1)
            return;

        betaSession.sendPacket(new EntityActionPacketData(entityId, newState));
    }
}
