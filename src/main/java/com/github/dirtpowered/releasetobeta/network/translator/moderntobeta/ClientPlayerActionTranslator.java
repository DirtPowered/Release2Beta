package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.packet.data.BlockDigPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerActionTranslator implements ModernToBeta<ClientPlayerActionPacket> {

    /*
     * Started digging 	 0
     * Finished digging  2
     * Drop item 	     4
     * Shoot arrow 	     5
     */

    @Override
    public void translate(ClientPlayerActionPacket packet, Session modernSession, BetaClientSession betaSession) {
        PlayerAction action = packet.getAction();
        Position pos = packet.getPosition();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        int face = MagicValues.value(Integer.class, packet.getFace());
        int newAction;

        switch (action) {
            case START_DIGGING:
                newAction = 0;
                break;
            case FINISH_DIGGING:
                newAction = 2;
                break;
            case DROP_ITEM:
                newAction = 4;
                break;
            case RELEASE_USE_ITEM:
                newAction = 5;
                break;
            default:
                newAction = -1;
                break;
        }

        if (newAction == -1)
            return;

        betaSession.sendPacket(new BlockDigPacketData(x, y, z, face, newAction));
    }
}
