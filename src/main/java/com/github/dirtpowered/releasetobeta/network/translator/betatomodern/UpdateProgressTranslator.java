package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.UpdateProgressPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowPropertyPacket;
import com.github.steveice10.packetlib.Session;

public class UpdateProgressTranslator implements BetaToModern<UpdateProgressPacketData> {

    @Override
    public void translate(UpdateProgressPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        int windowId = packet.getWindowId();
        int property = packet.getProgressBar();
        int value = packet.getProgressBarValue();

        //TODO: Calculate values
        // - We need to get fuel burn time somehow
        modernSession.send(new ServerWindowPropertyPacket(windowId, property, value));
    }
}
