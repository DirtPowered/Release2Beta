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

public class BetaChunk {
    private byte[] types;
    private NibbleArray metadata;
    private NibbleArray blocklight;
    private NibbleArray skylight;

    @Getter
    private int x, z;

    public BetaChunk(int x, int z) {
        this.x = x;
        this.z = z;

        this.types = new byte[Constants.MAX_CHUNK_SIZE];
        this.metadata = new NibbleArray(Constants.MAX_CHUNK_SIZE);
        this.blocklight = new NibbleArray(Constants.MAX_CHUNK_SIZE);
        this.skylight = new NibbleArray(Constants.MAX_CHUNK_SIZE);
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
