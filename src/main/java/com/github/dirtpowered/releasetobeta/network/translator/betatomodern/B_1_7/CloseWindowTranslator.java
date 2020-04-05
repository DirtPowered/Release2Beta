package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.CloseWindowPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerCloseWindowPacket;
import com.github.steveice10.packetlib.Session;

public class CloseWindowTranslator implements BetaToModern<CloseWindowPacketData> {

    @Override
    public void translate(CloseWindowPacketData packet, BetaClientSession session, Session modernSession) {
        int windowId = packet.getWindowId();

        modernSession.send(new ServerCloseWindowPacket(windowId));
    }
}
