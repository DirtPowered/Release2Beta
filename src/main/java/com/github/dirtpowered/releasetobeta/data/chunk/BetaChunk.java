/*
 * Copyright (c) 2020 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.releasetobeta.data.chunk;

import com.github.dirtpowered.releasetobeta.data.Constants;
import lombok.Getter;

import java.util.Arrays;

public class BetaChunk {
    private byte[] types;
    private NibbleArray metadata;
    private NibbleArray blocklight;
    private NibbleArray skylight;

    @Getter
    private int x, z, rawX, rawZ;

    public BetaChunk(int x, int z, int rawX, int rawZ) {
        this.x = x;
        this.z = z;

        this.rawX = rawX;
        this.rawZ = rawZ;

        this.types = new byte[Constants.MAX_CHUNK_SIZE];

        /* pre-sized arrays are filled with 0 by default. That's colliding with AIR block ID */
        Arrays.fill(types, (byte) -1);

        this.metadata = new NibbleArray(Constants.MAX_CHUNK_SIZE);
        this.blocklight = new NibbleArray(Constants.MAX_CHUNK_SIZE);
        this.skylight = new NibbleArray(Constants.MAX_CHUNK_SIZE);
    }

    private int getIndex(int x, int y, int z) {
        return x << 11 | z << 7 | y;
    }

    public int getTypeAt(int x, int y, int z) {
        return this.types[getIndex(x, y, z)] & 255;
    }

    public int getMetadataAt(int x, int y, int z) {
        return this.metadata.getNibble(x, y, z);
    }

    public int getBlockLightAt(int x, int y, int z) {
        return this.blocklight.getNibble(x, y, z);
    }

    public int getSkyLightAt(int x, int y, int z) {
        return this.skylight.getNibble(x, y, z);
    }

    public void setTypeAt(int x, int y, int z, int id) {
        this.types[getIndex(x, y, z)] = (byte) id;
    }

    public void setMetadataAt(int x, int y, int z, int data) {
        this.metadata.setNibble(x, y, z, (byte) data);
    }

    public int setChunkData(byte[] data, int x, int y, int z, int xSize, int ySize, int zSize, int totalSize, boolean skylight) {
        for (int i = x; i < xSize; ++i) {
            for (int j = z; j < zSize; ++j) {
                int index = i << 11 | j << 7 | y;
                int size = ySize - y;
                System.arraycopy(data, totalSize, this.types, index, size);
                totalSize += size;
            }
        }

        for (int i = x; i < xSize; ++i) {
            for (int j = z; j < zSize; ++j) {
                int index = (i << 11 | j << 7 | y) >> 1;
                int size = (ySize - y) / 2;
                System.arraycopy(data, totalSize, this.metadata.getData(), index, size);
                totalSize += size;
            }
        }

        for (int i = x; i < xSize; ++i) {
            for (int j = z; j < zSize; ++j) {
                int index = (i << 11 | j << 7 | y) >> 1;
                int size = (ySize - y) / 2;
                System.arraycopy(data, totalSize, this.blocklight.getData(), index, size);
                totalSize += size;
            }
        }

        if (skylight) {
            for (int i = x; i < xSize; ++i) {
                for (int j = z; j < zSize; ++j) {
                    int index = (i << 11 | j << 7 | y) >> 1;
                    int size = (ySize - y) / 2;
                    System.arraycopy(data, totalSize, this.skylight.getData(), index, size);
                    totalSize += size;
                }
            }
        }

        return totalSize;
    }
}
