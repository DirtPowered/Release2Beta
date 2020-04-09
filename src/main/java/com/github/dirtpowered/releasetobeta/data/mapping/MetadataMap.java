package com.github.dirtpowered.releasetobeta.data.mapping;

import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;

public class MetadataMap extends Remapper<DataObject> {

    public MetadataMap() {
        remap(90, new DataObject(0, 1, MinecraftVersion.B_1_8_1, MinecraftVersion.B_1_7_3));
        remap(61, new DataObject(12, 1, MinecraftVersion.B_1_8_1));
        remap(18, new DataObject(4, 0, MinecraftVersion.B_1_8_1));
        remap(29, new DataObject(7, 0, MinecraftVersion.B_1_8_1));
        remap(106, new DataObject(12, 0, MinecraftVersion.B_1_8_1));
    }
}
