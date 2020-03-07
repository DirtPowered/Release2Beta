package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.packet.data.BlockPlacePacketData;
import com.github.dirtpowered.betaprotocollib.utils.BetaItemStack;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerPlaceBlockTranslator implements ModernToBeta<ClientPlayerPlaceBlockPacket> {

    @Override
    public void translate(ClientPlayerPlaceBlockPacket packet, Session modernSession, BetaClientSession betaSession) {
        Position pos = packet.getPosition();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        int face = MagicValues.value(Integer.class, packet.getFace());
        betaSession.sendPacket(new BlockPlacePacketData(x, y, z, face, new BetaItemStack(0, 0, 0)));
    }
}
