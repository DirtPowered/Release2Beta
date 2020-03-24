package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.MapChunkPacketData;
import com.github.dirtpowered.releasetobeta.data.Constants;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
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
                chunks[i] = translateChunk(chunkX, chunkZ, session, data, (i * 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        modernSession.send(new ServerChunkDataPacket(new Column(chunkX, chunkZ, chunks, null)));
    }

    private Chunk translateChunk(int chunkX, int chunkZ, BetaClientSession session, byte[] chunk, int height) {
        byte[] types = getTypes(chunk);
        byte[] metadata = getMetadata(chunk, types.length);
        byte[] blockLight = getBlockLight(chunk);
        byte[] skyLight = getSkyLight(chunk);

        BlockStorage storage = new BlockStorage();
        NibbleArray3d nibbleBlockLight = new NibbleArray3d(4096);
        NibbleArray3d nibbleSkyLight = new NibbleArray3d(4096);

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int typeIndex = (Math.min(16, x) * 16 + Math.min(16, z)) * 128 + Math.min(128, y + height);
                    int blockId = types[typeIndex];
                    int blockData = metadata[typeIndex / 0x2];
                    //TODO: spawn tile entities too (chests, furnaces)
                    if (Utils.isTileEntity(blockId)) {
                        session.queueBlockChange(new BlockChangeRecord(
                                new Position(chunkX * 16 + x, y + height, chunkZ * 16 + z), new BlockState(blockId, 2 /* TODO: get correct face */)));
                    }
                    storage.set(x, y, z, new BlockState(blockId, blockData == 16 ? 0 : blockData));
                    nibbleBlockLight.set(x, y, z, blockLight[typeIndex / 0x2]);
                    nibbleSkyLight.set(x, y, z, skyLight[typeIndex / 0x2]);
                }
            }
        }

        return new Chunk(storage, nibbleBlockLight, nibbleSkyLight);
    }

    private byte[] getTypes(byte[] data) {
        byte[] types = new byte[Constants.MAX_CHUNK_SIZE];
        System.arraycopy(data, 0, types, 0, types.length);

        return types;
    }

    private byte[] getMetadata(byte[] data, int typesLength) {
        byte[] metadata = new byte[Constants.MAX_CHUNK_SIZE];
        System.arraycopy(data, typesLength, metadata, 0, metadata.length);

        return metadata;
    }

    private byte[] getBlockLight(byte[] data) {
        byte[] blockLight = new byte[Constants.MAX_CHUNK_SIZE];
        Arrays.fill(blockLight, (byte) 0x0A); //temp fix for black chunks
        return blockLight;
    }

    private byte[] getSkyLight(byte[] data) {
        return new byte[Constants.MAX_CHUNK_SIZE];
    }
}
