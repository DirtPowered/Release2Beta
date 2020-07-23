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

package com.github.dirtpowered.releasetobeta.data.blockstorage;

import com.github.dirtpowered.betaprotocollib.utils.BlockLocation;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;

public class BlockConnector {

    public Chunk connectBlocks(TempBlockStorage blockStorage, int chunkX, int chunkZ, Chunk chunk) {
        long hashKey = Utils.coordsToLong(chunkX, chunkZ);

        DataBlock[] blockList = blockStorage.getBlockStorageMap().get(hashKey);
        if (blockList == null) {
            return chunk;
        }

        for (DataBlock cachedBlock : blockList) {
            // snow layer connection
            if (cachedBlock.getLegacyBlockId() == 78) { // snow layer (legacy)
                BlockLocation b = cachedBlock.getBlockLocation();

                int blockBelow = blockStorage.getCachedBlockAt(new BlockLocation(b.getX(), b.getY() - 1, b.getZ())).getLegacyBlockId();
                if (blockBelow == 2) { // grass block (legacy)
                    try {
                        BlockLocation pos = cachedBlock.getChunkPosition();
                        chunk.set(pos.getX(), pos.getY() - 1, pos.getZ(), new BlockState(8)); // snowy grass
                    } catch (IndexOutOfBoundsException ignored) {
                        // ignored
                        //TODO: Update neighbor chunks
                    }
                }
            }
            // end
        }

        // remove cached chunk to free up some memory
        blockStorage.remove(chunkX, chunkZ);
        return chunk;
    }
}
