package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PreChunkPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUnloadChunkPacket;
import com.github.steveice10.packetlib.Session;

import java.util.Arrays;

public class PreChunkTranslator implements BetaToModern<PreChunkPacketData> {

    @Override
    public void translate(PreChunkPacketData packet, BetaClientSession session, Session modernSession) {
        int x = packet.getX();
        int z = packet.getZ();

        if (packet.isFull()) {
            byte[] biomes = new byte[256];
            Arrays.fill(biomes, (byte) 129); //mutated_plains (most close to the beta default one)

            Column column = new Column(x, z, new Chunk[16], biomes, null); //TODO: Send chests, furnaces
            modernSession.send(new ServerChunkDataPacket(column));
        } else {
            //unload
            modernSession.send(new ServerUnloadChunkPacket(x, z));
        }
    }
}