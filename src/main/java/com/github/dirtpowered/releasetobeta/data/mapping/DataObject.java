package com.github.dirtpowered.releasetobeta.data.mapping;

import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import lombok.Getter;

@Getter
public class DataObject {
    private int from;
    private int to;
    private MinecraftVersion[] minecraftVersion;

    DataObject(int from, int to, MinecraftVersion... minecraftVersion) {
        this.from = from;
        this.to = to;
        this.minecraftVersion = minecraftVersion;
    }
}
