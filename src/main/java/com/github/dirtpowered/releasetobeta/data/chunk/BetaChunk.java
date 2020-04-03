package com.github.dirtpowered.releasetobeta.data.chunk;

import com.github.dirtpowered.releasetobeta.data.Constants;

public class BetaChunk {
    private byte[] types;
    private NibbleArray metadata;
    private NibbleArray blocklight;
    private NibbleArray skylight;
    private int x;
    private int z;

    public BetaChunk(int x, int z) {
        this.x = x;
        this.z = z;

        this.types = new byte[Constants.MAX_CHUNK_SIZE];
        this.metadata = new NibbleArray(Constants.MAX_CHUNK_SIZE);
        this.blocklight = new NibbleArray(Constants.MAX_CHUNK_SIZE);
        this.skylight = new NibbleArray(Constants.MAX_CHUNK_SIZE);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    private int getIndex(int x, int y, int z) {
        return x << 11 | z << 7 | y;
    }

    public int getTypeAt(int x, int y, int z) {
        return types[getIndex(x, y, z)];
    }

    public int getMetadataAt(int x, int y, int z) {
        return metadata.getNibble(x, y, z);
    }

    public int getBlockLightAt(int x, int y, int z) {
        return blocklight.getNibble(x, y, z);
    }

    public int getSkyLightAt(int x, int y, int z) {
        return skylight.getNibble(x, y, z);
    }

    public void fillData(byte[] data, boolean withSkyLight) {
        int metadataOffset = types.length;
        int blockLightOffset = types.length + metadata.getData().length;
        int skyLightOffset = data.length - blocklight.getData().length;

        System.arraycopy(data, 0, types, 0, types.length);
        System.arraycopy(data, metadataOffset, metadata.getData(), 0, metadata.getData().length);
        System.arraycopy(data, blockLightOffset, blocklight.getData(), 0, blocklight.getData().length);

        if (withSkyLight) {
            System.arraycopy(data, skyLightOffset, skylight.getData(), 0, skylight.getData().length);
        }
    }
}
