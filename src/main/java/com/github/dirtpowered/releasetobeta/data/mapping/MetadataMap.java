package com.github.dirtpowered.releasetobeta.data.mapping;

public class MetadataMap extends Remapper<DataObject> {

    public MetadataMap() {
        remap(90, new DataObject(0, 1));
        remap(61, new DataObject(12, 1));
        remap(18, new DataObject(4, 0));
        remap(29, new DataObject(7, 0));
        remap(106, new DataObject(12, 0));
    }
}
