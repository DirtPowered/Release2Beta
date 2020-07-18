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

import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntityChicken;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntityCow;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntityMooshroomCow;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntityPig;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntitySheep;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntitySquid;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntityVillager;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntityWolf;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityBlaze;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityCaveSpider;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityCreeper;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityEnderDragon;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityEnderman;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityGhast;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityMagmaCube;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityPigZombie;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntitySilverfish;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntitySkeleton;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntitySlime;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntitySnowman;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntitySpider;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityZombie;
import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;

import java.util.HashMap;
import java.util.Map;

public class EntityRegistry {
    private Map<MobType, Class<? extends Entity>> entities = new HashMap<>();

    public EntityRegistry() {
        registerEntities();
    }

    private void registerEntities() {
        //monsters

        //1.6-1.7
        register(MobType.SKELETON, EntitySkeleton.class, MinecraftVersion.B_1_6_6);
        register(MobType.ZOMBIE, EntityZombie.class, MinecraftVersion.B_1_6_6);
        register(MobType.SPIDER, EntitySpider.class, MinecraftVersion.B_1_6_6);
        register(MobType.ZOMBIE_PIGMAN, EntityPigZombie.class, MinecraftVersion.B_1_6_6);
        register(MobType.GHAST, EntityGhast.class, MinecraftVersion.B_1_6_6);
        register(MobType.CREEPER, EntityCreeper.class, MinecraftVersion.B_1_6_6);
        register(MobType.SLIME, EntitySlime.class, MinecraftVersion.B_1_6_6);
        register(MobType.GIANT, EntityZombie.class, MinecraftVersion.B_1_6_6);

        //1.8
        register(MobType.ENDERMAN, EntityEnderman.class, MinecraftVersion.B_1_8_1);
        register(MobType.SILVERFISH, EntitySilverfish.class, MinecraftVersion.B_1_8_1);
        register(MobType.CAVE_SPIDER, EntityCaveSpider.class, MinecraftVersion.B_1_8_1);

        //1.9
        register(MobType.BLAZE, EntityBlaze.class, MinecraftVersion.B_1_9);
        register(MobType.ENDER_DRAGON, EntityEnderDragon.class, MinecraftVersion.B_1_9);
        register(MobType.MAGMA_CUBE, EntityMagmaCube.class, MinecraftVersion.B_1_9);
        register(MobType.SNOW_GOLEM, EntitySnowman.class, MinecraftVersion.B_1_9);

        //mobs
        register(MobType.PIG, EntityPig.class, MinecraftVersion.B_1_6_6);
        register(MobType.COW, EntityCow.class, MinecraftVersion.B_1_6_6);
        register(MobType.SHEEP, EntitySheep.class, MinecraftVersion.B_1_6_6);
        register(MobType.CHICKEN, EntityChicken.class, MinecraftVersion.B_1_6_6);
        register(MobType.SQUID, EntitySquid.class, MinecraftVersion.B_1_6_6);
        register(MobType.WOLF, EntityWolf.class, MinecraftVersion.B_1_6_6);

        //1.9
        register(MobType.MOOSHROOM, EntityMooshroomCow.class, MinecraftVersion.B_1_9);
        register(MobType.VILLAGER, EntityVillager.class, MinecraftVersion.B_1_9);
    }

    private void register(MobType type, Class<? extends Entity> entityClazz, MinecraftVersion version) {
        if (version.isNewerOrEqual(R2BConfiguration.version)) {
            entities.put(type, entityClazz);
        }
    }

    public Class<? extends Entity> getEntityFromMobType(MobType type) {
        return entities.getOrDefault(type, DummyEntity.class);
    }
}
