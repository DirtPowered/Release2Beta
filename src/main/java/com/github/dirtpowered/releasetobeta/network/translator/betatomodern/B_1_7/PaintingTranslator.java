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
