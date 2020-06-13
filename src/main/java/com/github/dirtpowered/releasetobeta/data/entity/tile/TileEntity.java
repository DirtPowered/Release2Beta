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

package com.github.dirtpowered.releasetobeta.data.entity.tile;

import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import io.netty.util.internal.StringUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class TileEntity {
    private static final Map<Integer, Class<? extends TileEntity>> tileEntities = new HashMap<>();
    private final static String KEY_PREFIX = "minecraft:";
    private final String minecraftKey;

    static {
        register(26, TileEntityBed.class, MinecraftVersion.B_1_6_6);
        register(52, TileEntityMobSpawner.class, MinecraftVersion.B_1_6_6);
        register(54, TileEntityChest.class, MinecraftVersion.B_1_6_6);
        register(116, TileEntityEnchantingTable.class, MinecraftVersion.B_1_9);
        register(119, TileEntityEndPortal.class, MinecraftVersion.B_1_9);
    }

    TileEntity(String minecraftKey) {
        this.minecraftKey = KEY_PREFIX + minecraftKey;
    }

    public static TileEntity create(int id) {
        try {
            Class<? extends TileEntity> oclass = TileEntity.tileEntities.get(id);

            if (oclass != null)
                return oclass.newInstance();

        } catch (Exception e) {
            System.out.println("Failed to create block entity!");
        }

        return null;
    }

    public CompoundTag getNBT(Position position) {
        CompoundTag tileTag = new CompoundTag(StringUtil.EMPTY_STRING);
        tileTag.put(new StringTag("id", getMinecraftKey()));

        tileTag.put(new IntTag("x", position.getX()));
        tileTag.put(new IntTag("y", position.getY()));
        tileTag.put(new IntTag("z", position.getZ()));
        return tileTag;
    }

    public static boolean containsId(int id) {
        return tileEntities.containsKey(id);
    }

    private static void register(int id, Class<? extends TileEntity> entityClazz, MinecraftVersion version) {
        if (version.isNewerOrEqual(R2BConfiguration.version)) {
            tileEntities.put(id, entityClazz);
        }
    }
}
