package com.github.dirtpowered.releasetobeta.data.chunk;

public class NibbleArray {
    private final byte[] data;

    NibbleArray(int initialSize) {
        this.data = new byte[initialSize >> 1];
    }

    int getNibble(int x, int y, int z) {
        int index = x << 11 | z << 7 | y;
        int value = index >> 1;
        boolean above = (index & 1) == 0;
        return above ? this.data[value] & 15 : this.data[value] >> 4 & 15;
    }

    public byte[] getData() {
        return data;
    }
}
