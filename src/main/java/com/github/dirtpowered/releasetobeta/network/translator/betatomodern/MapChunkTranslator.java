package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.MapChunkPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.packetlib.Session;

public class MapChunkTranslator implements BetaToModern<MapChunkPacketData> {

    //modernSession.send(new ServerBlockChangePacket(new BlockChangeRecord(new Position(x + chunkX, y + columnCurrentHeight, z + chunkZ), new BlockState(data[index], 0))));

    @Override
    public void translate(MapChunkPacketData packet, BetaClientSession session, Session modernSession) {
        int chunkX = packet.getX() / 16;
        int chunkZ = packet.getZ() / 16;
        int height = packet.getY();
        if (height > 0)
            return; //skip that weird chunks

        byte[] data = packet.getChunk();

        Chunk[] chunks = new Chunk[16];
        for (int i = 0; i < 8; i++) { //8 chunks (max y = 128)
            try {
                chunks[i] = translateChunk(data, (i * 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        modernSession.send(new ServerChunkDataPacket(new Column(chunkX, chunkZ, chunks, null)));
    }

    private Chunk translateChunk(byte[] chunk, int height) {
        BlockStorage storage = new BlockStorage();
        NibbleArray3d blockLight = new NibbleArray3d(4096);

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int typeIndex = (Math.min(16, x) * 16 + Math.min(16, z)) * 128 + Math.min(128, y + height);
                    int blockId = chunk[typeIndex];
                    int blockData = 0;
                    storage.set(x, y, z, new BlockState(blockId, blockData));
                    blockLight.set(x, y, z, getLight(chunk, x, y, z));
                }
            }
        }

        return new Chunk(storage, blockLight, new NibbleArray3d(4096));
    }

    private int getLight(byte[] data, int x, int y, int z) {
        return 15;
    }
}
