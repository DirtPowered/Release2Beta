/*
 * Copyright (c) 2020 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.BlockDigPacketData;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
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
    public void translate(ReleaseToBeta main, ClientPlayerActionPacket packet, Session modernSession, BetaClientSession betaSession) {
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
