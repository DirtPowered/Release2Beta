package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.BlockItemSwitchPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket;
import com.github.steveice10.packetlib.Session;

public class BlockItemSwitchTranslator implements BetaToModern<BlockItemSwitchPacketData> {

    @Override
    public void translate(BlockItemSwitchPacketData packet, BetaClientSession session, Session modernSession) {
        int slot = packet.getSlot();
        session.getPlayer().getInventory().setCurrentHotbarSlot(slot);

        modernSession.send(new ServerPlayerChangeHeldItemPacket(slot));
    }
}
