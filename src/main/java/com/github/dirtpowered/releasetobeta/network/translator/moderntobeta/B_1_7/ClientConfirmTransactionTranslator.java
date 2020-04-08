package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.TransactionPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import com.github.steveice10.packetlib.Session;

public class ClientConfirmTransactionTranslator implements ModernToBeta<ClientConfirmTransactionPacket> {

    @Override
    public void translate(ClientConfirmTransactionPacket packet, Session modernSession, BetaClientSession betaSession) {
        int windowId = packet.getWindowId();
        int actionId = packet.getActionId();
        boolean accepted = packet.getAccepted();

        betaSession.sendPacket(new TransactionPacketData(windowId, actionId, accepted));
    }
}
