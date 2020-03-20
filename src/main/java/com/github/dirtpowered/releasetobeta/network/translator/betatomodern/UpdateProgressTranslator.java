package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.UpdateProgressPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowPropertyPacket;
import com.github.steveice10.packetlib.Session;

public class UpdateProgressTranslator implements BetaToModern<UpdateProgressPacketData> {

    @Override
    public void translate(UpdateProgressPacketData packet, BetaClientSession session, Session modernSession) {
        int windowId = packet.getWindowId();
        int property = packet.getProgressBar();
        int value = property == 0 ? packet.getProgressBarValue() : 0;

        if (property == 2) {
            //TODO: send max-progress once
            modernSession.send(new ServerWindowPropertyPacket(windowId, 3, 200)); //max progress
        } else if (property == 1) {
            modernSession.send(new ServerWindowPropertyPacket(windowId, 0, packet.getProgressBarValue() * 200 / 1600)); //fuel left
            return;
        }

        int newProperty = property == 0 ? 2 : 0;
        modernSession.send(new ServerWindowPropertyPacket(windowId, property == 2 ? 1 : newProperty, value));
    }
}
