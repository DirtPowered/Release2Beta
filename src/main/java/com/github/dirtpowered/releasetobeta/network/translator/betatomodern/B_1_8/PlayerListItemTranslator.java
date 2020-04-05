package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.PlayerListItemPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.packetlib.Session;

public class PlayerListItemTranslator implements BetaToModern<PlayerListItemPacketData> {

    @Override
    public void translate(PlayerListItemPacketData packet, BetaClientSession session, Session modernSession) {
        String username = packet.getUsername();
        boolean online = packet.isOnline();

        if (online) {
            //TODO: add tab entry
        } else {
            //TODO: remove tab entry
        }
    }
}
