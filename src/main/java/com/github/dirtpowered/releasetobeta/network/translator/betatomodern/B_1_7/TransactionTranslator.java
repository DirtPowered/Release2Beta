package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.TransactionPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerConfirmTransactionPacket;
import com.github.steveice10.packetlib.Session;

public class TransactionTranslator implements BetaToModern<TransactionPacketData> {

    @Override
    public void translate(TransactionPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);

        int windowId = packet.getWindowId();
        int actionId = packet.getShortWindowId();
        boolean accepted = packet.isAccepted();

        modernSession.send(new ServerConfirmTransactionPacket(windowId, actionId, accepted));
    }
}
