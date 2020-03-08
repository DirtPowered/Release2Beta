package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.packet.data.BlockPlacePacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerUseItemTranslator implements ModernToBeta<ClientPlayerUseItemPacket> {

    /**
     * This packet has a special case where X, Y, Z, and Direction are all -1.
     * This special packet indicates that the currently held item for the player
     * should have its state updated such as eating food, shooting bows, using buckets, etc.
     * <p>
     * https://wiki.vg/index.php?title=Protocol&oldid=689#Player_Block_Placement_.280x0F.29
     */

    @Override
    public void translate(ClientPlayerUseItemPacket packet, Session modernSession, BetaClientSession betaSession) {
        betaSession.sendPacket(new BlockPlacePacketData(-1, -1, -1, -1, new BetaItemStack()));
    }
}
