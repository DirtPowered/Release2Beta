package com.github.dirtpowered.releasetobeta.data.chunk;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ModernChunk {
    private Chunk chunk;
    private List<CompoundTag> chunkTileEntities;
    private NibbleArray3d blockLight;
    private NibbleArray3d skyLight;
}
