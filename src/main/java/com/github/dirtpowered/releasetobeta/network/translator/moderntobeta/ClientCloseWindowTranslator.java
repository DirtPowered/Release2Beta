package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.packet.data.CloseWindowPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import com.github.steveice10.packetlib.Session;

public class ClientCloseWindowTranslator implements ModernToBeta<ClientCloseWindowPacket> {

    @Override
    public void translate(ClientCloseWindowPacket packet, Session modernSession, BetaClientSession betaSession) {
        int windowId = packet.getWindowId();

        betaSession.sendPacket(new CloseWindowPacketData(windowId));
    }
}
