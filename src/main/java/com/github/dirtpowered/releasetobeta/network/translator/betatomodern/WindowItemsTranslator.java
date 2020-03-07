package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.WindowItemsPacketData;
import com.github.dirtpowered.betaprotocollib.utils.BetaItemStack;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.packetlib.Session;

public class WindowItemsTranslator implements BetaToModern<WindowItemsPacketData> {

    @Override
    public void translate(WindowItemsPacketData packet, BetaClientSession session, Session modernSession) {
        int windowId = packet.getWindowId();
        BetaItemStack[] items = packet.getItemStacks();

        modernSession.send(new ServerWindowItemsPacket(windowId, Utils.convertItemStacks(items)));
    }
}
