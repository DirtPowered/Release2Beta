package com.github.dirtpowered.releasetobeta.data.entity;

import java.util.Arrays;

public enum TileEntity {

    CHEST(54), FURNACE(61), MOB_SPAWNER(52);

    private int blockId;

    TileEntity(int blockId) {
        this.blockId = blockId;
    }

    public static boolean isTileEntity(int blockId) {
        return Arrays.stream(values()).anyMatch(tileEntity -> {
            return blockId == tileEntity.blockId;
        });
    }

    public static TileEntity getFromId(int blockId) {
        return Arrays.stream(TileEntity.values()).filter(tileEntity -> {
            return tileEntity.blockId == blockId;
        }).findFirst().orElse(null);
    }

    public int getBlockId() {
        return blockId;
    }
}
