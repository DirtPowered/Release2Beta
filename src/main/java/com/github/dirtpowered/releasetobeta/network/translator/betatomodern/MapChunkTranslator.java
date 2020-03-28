package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.MapChunkPacketData;
import com.github.dirtpowered.releasetobeta.data.chunk.BetaChunk;
import com.github.dirtpowered.releasetobeta.data.entity.TileEntity;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.packetlib.Session;
import org.pmw.tinylog.Logger;

public class MapChunkTranslator implements BetaToModern<MapChunkPacketData> {

    @Override
    public void translate(MapChunkPacketData packet, BetaClientSession session, Session modernSession) {
        boolean skylight = session.getPlayer().getDimension() == 0;

        int chunkX = packet.getX() / 16;
        int chunkY = packet.getY();
        int chunkZ = packet.getZ() / 16;

        if (chunkY > 0)
            return; //skip that weird chunks

        BetaChunk chunk = new BetaChunk(chunkX, chunkZ);
        try {
            chunk.fillData(packet.getChunk(), skylight);
            Chunk[] chunks = new Chunk[16];
            for (int i = 0; i < 8; i++) { //8 chunks (max y = 128)
                chunks[i] = translateChunk(session, chunk, i * 16, skylight);
            }

            modernSession.send(new ServerChunkDataPacket(new Column(chunkX, chunkZ, chunks, null)));
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.warn("Chunk at [x={} z={}] was skipped", Utils.fromChunkPos(chunk.getX()), Utils.fromChunkPos(chunk.getZ()));
        }
    }

    private Chunk translateChunk(BetaClientSession session, BetaChunk chunk, int height, boolean skylight) {
        BlockStorage storage = new BlockStorage();
        NibbleArray3d nibbleBlockLight = new NibbleArray3d(4096);
        NibbleArray3d nibbleSkyLight = new NibbleArray3d(4096);

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int yh = y + height;

                    int blockId = session.remapBlock(chunk.getTypeAt(x, yh, z));
                    int blockData = chunk.getMetadataAt(x, yh, z);
                    int blockLight = chunk.getBlockLightAt(x, yh, z);
                    int skyLight = chunk.getSkyLightAt(x, yh, z);

                    if (TileEntity.isTileEntity(blockId)) {
                        session.queueBlockChange(
                                Utils.fromChunkPos(chunk.getX()) + x, y + height,
                                Utils.fromChunkPos(chunk.getZ()) + z, blockId, blockData
                        );
                    }

                    storage.set(x, y, z, new BlockState(blockId, blockData));
                    nibbleBlockLight.set(x, y, z, blockLight);
                    nibbleSkyLight.set(x, y, z, skyLight);
                }
            }
        }

        return new Chunk(storage, nibbleBlockLight, skylight ? nibbleSkyLight : null);
    }
}
