package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.ExplosionPacketData;
import com.github.dirtpowered.betaprotocollib.utils.BlockLocation;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.world.block.ExplodedBlockRecord;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerExplosionPacket;
import com.github.steveice10.packetlib.Session;

import java.util.ArrayList;
import java.util.List;

public class ExplosionTranslator implements BetaToModern<ExplosionPacketData> {

    @Override
    public void translate(ExplosionPacketData packet, BetaClientSession session, Session modernSession) {
        float x = Utils.toFloat(packet.getX());
        float y = Utils.toFloat(packet.getY());
        float z = Utils.toFloat(packet.getZ());

        float explosionSize = packet.getExplosionSize();

        List<ExplodedBlockRecord> records = new ArrayList<>();
        for (BlockLocation destroyedBlockPosition : packet.getDestroyedBlockPositions()) {
            int posX = destroyedBlockPosition.getX();
            int posY = destroyedBlockPosition.getY();
            int posZ = destroyedBlockPosition.getZ();

            records.add(new ExplodedBlockRecord(posX, posY, posZ));
        }

        modernSession.send(new ServerExplosionPacket(x, y, z, explosionSize, records, 0, 0, 0));
    }
}
