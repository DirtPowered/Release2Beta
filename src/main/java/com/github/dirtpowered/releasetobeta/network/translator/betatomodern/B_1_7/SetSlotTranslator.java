package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.SetSlotPacketData;
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

        if (itemStack != null)
            itemStack.setBlockId(session.remapBlock(itemStack.getBlockId()));

        modernSession.send(new ServerSetSlotPacket(windowId, itemSlot, Utils.betaItemStackToItemStack(itemStack)));
    }
}
