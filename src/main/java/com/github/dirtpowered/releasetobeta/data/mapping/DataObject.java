package com.github.dirtpowered.releasetobeta.data.mapping;

import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;

public class DataObject {
    private int from;
    private int to;
    private MinecraftVersion[] minecraftVersion;

    DataObject(int from, int to, MinecraftVersion... minecraftVersion) {
        this.from = from;
        this.to = to;
        this.minecraftVersion = minecraftVersion;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public MinecraftVersion[] getMinecraftVersion() {
        return minecraftVersion;
    }
}
