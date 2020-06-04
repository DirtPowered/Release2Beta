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

package com.github.dirtpowered.releasetobeta.data.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import io.netty.util.internal.StringUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum TileEntity {
    CHEST(54, "chest"),
    MOB_SPAWNER(52, "mob_spawner"),
    BED(26, "bed"),
    END_PORTAL(119, "end_portal"),
    ENCHANTING_TABLE(116, "enchanting_table");

    public final static String KEY_PREFIX = "minecraft:";

    private int blockId;
    private String blockName;

    TileEntity(int blockId, String name) {
        this.blockId = blockId;
        this.blockName = name;
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

    public static CompoundTag getTileMeta(TileEntity tileEntity, Position position) {
        Map<String, Tag> nbt = new HashMap<>();
        nbt.put("id", new StringTag("id", KEY_PREFIX + tileEntity.blockName));

        nbt.put("x", new IntTag("x", position.getX()));
        nbt.put("y", new IntTag("y", position.getY()));
        nbt.put("z", new IntTag("z", position.getZ()));

        return new CompoundTag(StringUtil.EMPTY_STRING, nbt);
    }
}
