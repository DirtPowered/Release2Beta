package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.BlockItemSwitchPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerChangeHeldItemTranslator implements ModernToBeta<ClientPlayerChangeHeldItemPacket> {

    @Override
    public void translate(ClientPlayerChangeHeldItemPacket packet, Session modernSession, BetaClientSession betaSession) {
        int slot = packet.getSlot();
        betaSession.getPlayer().getInventory().setCurrentHotbarSlot(slot);

        betaSession.sendPacket(new BlockItemSwitchPacketData(slot));
    }
}
