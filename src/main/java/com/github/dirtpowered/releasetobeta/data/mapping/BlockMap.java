package com.github.dirtpowered.releasetobeta.data.mapping;

public class BlockMap extends Remapper<Integer> {

    public BlockMap() {
        remap(95, 54); //locked_chest -> chest
    }
}
