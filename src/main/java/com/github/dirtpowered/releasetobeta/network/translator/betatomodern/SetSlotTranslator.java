package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.packet.data.SetSlotPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.packetlib.Session;

public class SetSlotTranslator implements BetaToModern<SetSlotPacketData> {

    @Override
    public void translate(SetSlotPacketData packet, BetaClientSession session, Session modernSession) {
        int windowId = packet.getWindowId();
        int itemSlot = packet.getItemSlot();

        BetaItemStack itemStack = packet.getItemStack();

        modernSession.send(new ServerSetSlotPacket(windowId, itemSlot, Utils.betaItemStackToItemStack(itemStack)));
    }
}
