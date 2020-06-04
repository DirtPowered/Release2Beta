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

package com.github.dirtpowered.releasetobeta.data.mapping;

import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import com.github.dirtpowered.releasetobeta.data.mapping.model.DataHolder;
import com.github.dirtpowered.releasetobeta.data.mapping.model.DataObject;

public class MetadataMap extends DataHolder<DataObject[]> {

    private final static int INVALID_DATA_VALUE = 2;

    public MetadataMap() {
        add(90, new DataObject[]{
                new DataObject(0, INVALID_DATA_VALUE, MinecraftVersion.B_1_8_1, MinecraftVersion.B_1_7_3, MinecraftVersion.B_1_6_6),
        });

        add(18, new DataObject[]{
                new DataObject(4, 0, MinecraftVersion.B_1_8_1, MinecraftVersion.B_1_9),
                new DataObject(5, 1, MinecraftVersion.B_1_8_1, MinecraftVersion.B_1_9),
                new DataObject(6, 2, MinecraftVersion.B_1_8_1, MinecraftVersion.B_1_9),
                new DataObject(7, 3, MinecraftVersion.B_1_8_1, MinecraftVersion.B_1_9),
        });

        add(106, new DataObject[]{
                new DataObject(1, 0, MinecraftVersion.B_1_8_1),
                new DataObject(12, 0, MinecraftVersion.B_1_8_1),
        });

        add(29, new DataObject[]{
                new DataObject(7, 0, MinecraftVersion.B_1_8_1, MinecraftVersion.B_1_9),
        });

        add(33, new DataObject[]{
                new DataObject(7, 0, MinecraftVersion.B_1_8_1, MinecraftVersion.B_1_9),
        });

        add(355, new DataObject[]{
                new DataObject(-1, 14, MinecraftVersion.B_1_8_1, MinecraftVersion.B_1_7_3, MinecraftVersion.B_1_6_6, MinecraftVersion.B_1_9),
        });

        add(126, new DataObject[]{
                new DataObject(2, 0, MinecraftVersion.B_1_8_1, MinecraftVersion.B_1_7_3, MinecraftVersion.B_1_6_6, MinecraftVersion.B_1_9),
        });

        add(64, new DataObject[]{
                new DataObject(15, 8, MinecraftVersion.B_1_7_3, MinecraftVersion.B_1_6_6),
                new DataObject(14, 8, MinecraftVersion.B_1_7_3, MinecraftVersion.B_1_6_6),
                new DataObject(13, 8, MinecraftVersion.B_1_7_3, MinecraftVersion.B_1_6_6),
                new DataObject(12, 8, MinecraftVersion.B_1_7_3, MinecraftVersion.B_1_6_6),
                new DataObject(3, 4, MinecraftVersion.B_1_7_3, MinecraftVersion.B_1_6_6),
                new DataObject(1, 6, MinecraftVersion.B_1_7_3, MinecraftVersion.B_1_6_6),
        });

        /*
         * Chest data in pre b1.8 is always 0, because chest (and nether portal) block face is calculated on client side
         * Below code fixes random turning chests into stone when using ViaVersion
         */
        add(54, new DataObject[]{
                new DataObject(0, INVALID_DATA_VALUE, MinecraftVersion.B_1_7_3, MinecraftVersion.B_1_6_6),
        });
    }
}
