package com.github.dirtpowered.releasetobeta.data.mapping;

public class MetadataMap extends Remapper<Integer> {

    public MetadataMap() {
        remap(90, 1); //nether portal fix
        remap(61, 2); //furnace fix
    }
}
