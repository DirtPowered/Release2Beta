package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.UpdateSignPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.packetlib.Session;

public class UpdateSignTranslator implements BetaToModern<UpdateSignPacketData> {

    @Override
    public void translate(UpdateSignPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);

        int x = packet.getX();
        int y = packet.getY();
        int z = packet.getZ();

        Position pos = new Position(x, y, z);
        //Beta server sending same packet when signs are near player.
        //modernSession.send(new ServerOpenTileEntityEditorPacket(pos));
    }
}
