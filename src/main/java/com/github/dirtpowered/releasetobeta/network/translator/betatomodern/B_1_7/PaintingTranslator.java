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

package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PaintingPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.type.PaintingType;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.HangingDirection;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPaintingPacket;
import com.github.steveice10.packetlib.Session;

import java.util.UUID;

public class PaintingTranslator implements BetaToModern<PaintingPacketData> {

    @Override
    public void translate(PaintingPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        UUID uuid = UUID.randomUUID();
        PaintingType paintingType = MagicValues.key(PaintingType.class, packet.getTitle());
        int direction = packet.getDirection();

        int x = packet.getX();
        int y = packet.getY();
        int z = packet.getZ();

        HangingDirection hangingDirection;

        switch (direction) {
            case 0:
                hangingDirection = HangingDirection.NORTH;
                z -= 1;
                break;
            case 1:
                hangingDirection = HangingDirection.WEST;
                x -= 1;
                break;
            case 2:
                hangingDirection = HangingDirection.SOUTH;
                z += 1;
                break;
            case 3:
                hangingDirection = HangingDirection.EAST;
                x += 1;
                break;
            default:
                hangingDirection = null;
                break;
        }

        Position pos = new Position(x, y, z);
        modernSession.send(new ServerSpawnPaintingPacket(entityId, uuid, paintingType, pos, hangingDirection));
    }
}
