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

package com.github.dirtpowered.releasetobeta.data.location;

import com.github.dirtpowered.betaprotocollib.utils.Location;
import com.github.dirtpowered.releasetobeta.data.Constants;
import com.github.dirtpowered.releasetobeta.data.blockstorage.TempBlockStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * based on <a href="https://github.com/Heath123">Heath123</a> work
 */
public class MovementTranslator {
    private Map<Integer, BoundingBox> items = new HashMap<>();

    public MovementTranslator() {
        register(54, new BoundingBox(0.5, 0.5, 0.5, 1, 1, 1));
        //TODO: add more
    }

    public Location translate(TempBlockStorage blockCache, double xPos, double yPos, double zPos) {
        BoundingBox boundingBox = new BoundingBox(xPos, yPos + 0.9D, zPos, 0.6D, Constants.PLAYER_HEIGHT, 0.6D);
        Location loc = new Location(xPos, yPos, zPos);

        int minX = (int) Math.floor(loc.getX() - 0.23D);
        int maxX = (int) Math.floor(loc.getX() + 0.23D);

        int minY = (int) Math.floor(loc.getY() - Constants.PLAYER_HEIGHT);
        int maxY = (int) Math.floor(loc.getY() + Constants.PLAYER_HEIGHT);

        int minZ = (int) Math.floor(loc.getZ() - 0.23D);
        int maxZ = (int) Math.floor(loc.getZ() + 0.23D);

        for (int y = minY; y < maxY + 1; y++) {
            for (int x = minX; x < maxX + 1; x++) {
                for (int z = minZ; z < maxZ + 1; z++) {
                    int blockId = blockCache.getCachedBlockAt(new Location(x, y, z)).getBlockState().getId();

                    if (items.containsKey(blockId)) {
                        return correctPosition(x, y, z, boundingBox, items.get(blockId));
                    }
                }
            }
        }

        return loc;
    }

    private Location correctPosition(int x, int y, int z, BoundingBox playerBox, BoundingBox blockBox) {
        double playerMinY = playerBox.getMiddleY() - (playerBox.getSizeY() / 2);
        double boxMaxY = (blockBox.getMiddleY() + y) + (blockBox.getSizeY() / 2);

        if (blockBox.checkIntersection(x, y, z, playerBox)) {
            //TODO: X, Z
            playerBox.updatePosition(0, boxMaxY - playerMinY, 0);
        }

        return new Location(playerBox.getMiddleX(), playerBox.getMiddleY() - 0.9D, playerBox.getMiddleZ());
    }

    private void register(int id, BoundingBox boundingBox) {
        items.put(id, boundingBox);
    }
}
