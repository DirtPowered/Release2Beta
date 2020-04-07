package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_8;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.CreativeItemGetPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCreativeInventoryActionPacket;
import com.github.steveice10.packetlib.Session;

public class ClientCreativeInventoryActionTranslator implements ModernToBeta<ClientCreativeInventoryActionPacket> {

    @Override
    public void translate(ClientCreativeInventoryActionPacket packet, Session modernSession, BetaClientSession betaSession) {
        Utils.debug(packet);
        betaSession.getMain().getScheduledExecutorService().execute(() -> {

            ItemStack itemStack = packet.getClickedItem();
            if (itemStack == null)
                return;

            betaSession.sendPacket(new CreativeItemGetPacketData((short) packet.getSlot(), Utils.itemStackToBetaItemStack(itemStack)));
        });
    }
}
