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

package com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections;

import com.github.dirtpowered.releasetobeta.data.blockstorage.blockconnections.model.BlockConnection;

public class ChestConnection implements BlockConnection {

    @Override
    public int connect(boolean west, boolean east, boolean north, boolean south, boolean up, boolean down, int originalData) {
        int data = originalData;

        // set b1.6-1.7 default facing to north
        if (originalData == 0) {
            data = 2;
        }

        // west | north-right (not facing south)
        if (!east && !north && !south && west && originalData != 3) {
            data = 6;
        }
        // east | north-left (not facing south)
        if (east && !north && !south && !west && originalData != 3) {
            data = 7;
        }
        // north | west-left (not facing east)
        if (!east && north && !south && !west && originalData != 5) {
            data = 8;
        }
        // south | west-right (not facing east)
        if (!east && !north && south && !west && originalData != 5) {
            data = 9;
        }
        // west | north-right (facing south)
        if (!east && !north && !south && west && originalData == 3) {
            data = 10;
        }
        // east | north-left (facing south)
        if (east && !north && !south && !west && originalData == 3) {
            data = 11;
        }
        // north | west-left (facing east)
        if (!east && north && !south && !west && originalData == 5) {
            data = 12;
        }
        // south | west-right (facing east)
        if (!east && !north && south && !west && originalData == 5) {
            data = 13;
        }

        return data;
    }
}
