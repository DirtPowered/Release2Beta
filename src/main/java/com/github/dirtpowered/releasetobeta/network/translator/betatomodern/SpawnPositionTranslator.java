package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.SpawnPositionPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerSpawnPositionPacket;
import com.github.steveice10.packetlib.Session;

public class SpawnPositionTranslator implements BetaToModern<SpawnPositionPacketData> {

    @Override
    public void translate(SpawnPositionPacketData packet, BetaClientSession session, Session modernSession) {
        int x = packet.getX();
        int y = packet.getY();
        int z = packet.getZ();

        modernSession.send(new ServerSpawnPositionPacket(new Position(x, y, z)));
    }
}
