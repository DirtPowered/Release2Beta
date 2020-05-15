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

        //1.7
        register(MobType.SKELETON, EntitySkeleton.class);
        register(MobType.ZOMBIE, EntityZombie.class);
        register(MobType.SPIDER, EntitySpider.class);
        register(MobType.ZOMBIE_PIGMAN, EntityPigZombie.class);
        register(MobType.GHAST, EntityGhast.class);
        register(MobType.CREEPER, EntityCreeper.class);
        register(MobType.SLIME, EntitySlime.class);
        register(MobType.GIANT_ZOMBIE, EntityZombie.class);

        //1.8
        register(MobType.ENDERMAN, EntityEnderman.class);
        register(MobType.SILVERFISH, EntitySilverfish.class);
        register(MobType.CAVE_SPIDER, EntityCaveSpider.class);

        //1.9
        register(MobType.BLAZE, EntityBlaze.class);
        register(MobType.ENDER_DRAGON, EntityEnderDragon.class);
        register(MobType.MAGMA_CUBE, EntityMagmaCube.class);
        register(MobType.SNOWMAN, EntitySnowman.class);

        //mobs
        register(MobType.PIG, EntityPig.class);
        register(MobType.COW, EntityCow.class);
        register(MobType.SHEEP, EntitySheep.class);
        register(MobType.CHICKEN, EntityChicken.class);
        register(MobType.SQUID, EntitySquid.class);
        register(MobType.WOLF, EntityWolf.class);

        //1.9
        register(MobType.MOOSHROOM, EntityMooshroomCow.class);
        register(MobType.VILLAGER, EntityVillager.class);
    }

    private void register(MobType type, Class<? extends Entity> entityClazz) {
        //TODO: game version depended registry
        entities.put(type, entityClazz);
    }

    public Class<? extends Entity> getEntityFromMobType(MobType type) {
        return entities.getOrDefault(type, DummyEntity.class);
    }
}
