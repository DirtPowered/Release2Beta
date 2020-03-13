package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.MapChunkPacketData;
import com.github.dirtpowered.releasetobeta.data.Constants;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.packetlib.Session;

import java.util.Arrays;

public class MapChunkTranslator implements BetaToModern<MapChunkPacketData> {

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
        byte[] metadata = getMetadata(chunk);
        byte[] blockLight = getLight(chunk, metadata.length);

        BlockStorage storage = new BlockStorage();
        NibbleArray3d nibbleLight = new NibbleArray3d(4096);

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {

                    int typeIndex = (Math.min(16, x) * 16 + Math.min(16, z)) * 128 + Math.min(128, y + height);
                    int blockId = chunk[typeIndex];
                    int blockData = metadata[typeIndex / 0x2];
                    //TODO: spawn tile entities too (chests, furnaces)
                    storage.set(x, y, z, new BlockState(blockId, blockData));
                    nibbleLight.set(x, y, z, blockLight[typeIndex]);
                }
            }
        }

        return new Chunk(storage, nibbleLight, new NibbleArray3d(4096));
    }

    private byte[] getLight(byte[] data, int metadataLength) {
        //TODO: Block Light
        byte[] blockLight = new byte[Constants.MAX_CHUNK_SIZE];

        Arrays.fill(blockLight, (byte) 0x08);
        return blockLight;
    }

    private byte[] getMetadata(byte[] data) {
        byte[] metadata = new byte[Constants.MAX_CHUNK_SIZE];
        System.arraycopy(data, Constants.MAX_CHUNK_SIZE, metadata, 0, metadata.length);

        return metadata;
    }
}
