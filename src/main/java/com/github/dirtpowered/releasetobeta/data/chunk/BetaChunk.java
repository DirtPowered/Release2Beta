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
        return this.types[getIndex(x, y, z)];
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

    public void fillData(byte[] data, boolean withSkyLight) {
        fillData(data, 0, 0, 0, 0, 0, 0, withSkyLight, true);
    }

    public void fillData(byte[] data, int x, int y, int z, int xSize, int ySize, int zSize, boolean withSkyLight, boolean full) {
        if (full) {
            int metadataOffset = types.length;
            int blockLightOffset = types.length + metadata.getData().length;
            int skyLightOffset = data.length - blocklight.getData().length;

            System.arraycopy(data, 0, types, 0, types.length);
            System.arraycopy(data, metadataOffset, metadata.getData(), 0, metadata.getData().length);
            System.arraycopy(data, blockLightOffset, blocklight.getData(), 0, blocklight.getData().length);

            if (withSkyLight) {
                System.arraycopy(data, skyLightOffset, skylight.getData(), 0, skylight.getData().length);
            }
        } else {
            int i;
            int j;
            int index;
            int length;

            int offset = 0;

            for (i = x; i < xSize; ++i) {
                for (j = z; j < zSize; ++j) {
                    index = i << 11 | j << 7 | y; // normal index
                    length = ySize - y;
                    System.arraycopy(data, offset, this.types, index, length);
                    offset += length;
                }
            }

            for (i = x; i < xSize; ++i) {
                for (j = z; j < zSize; ++j) {
                    index = (i << 11 | j << 7 | y) >> 1; // nibble index
                    length = (ySize - y) / 2;
                    System.arraycopy(data, offset, this.metadata.getData(), index, length);
                    offset += length;
                }
            }

            for (i = x; i < xSize; ++i) {
                for (j = z; j < zSize; ++j) {
                    index = (i << 11 | j << 7 | y) >> 1;
                    length = (ySize - y) / 2;
                    System.arraycopy(data, offset, this.blocklight.getData(), index, length);
                    offset += length;
                }
            }

            if (withSkyLight) {
                for (i = x; i < xSize; ++i) {
                    for (j = z; j < zSize; ++j) {
                        index = (i << 11 | j << 7 | y) >> 1;
                        length = (ySize - y) / 2;
                        System.arraycopy(data, offset, this.skylight.getData(), index, length);
                        offset += length;
                    }
                }
            }
        }
    }
}
