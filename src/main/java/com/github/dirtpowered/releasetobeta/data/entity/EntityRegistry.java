package com.github.dirtpowered.releasetobeta.data.entity;

import com.github.dirtpowered.releasetobeta.data.entity.mob.EntityChicken;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntityCow;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntityPig;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntitySheep;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntitySquid;
import com.github.dirtpowered.releasetobeta.data.entity.mob.EntityWolf;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityCreeper;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityGhast;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityPigZombie;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntitySkeleton;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntitySlime;
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
        register(MobType.SKELETON, EntitySkeleton.class);
        register(MobType.ZOMBIE, EntityZombie.class);
        register(MobType.SPIDER, EntitySpider.class);
        register(MobType.ZOMBIE_PIGMAN, EntityPigZombie.class);
        register(MobType.GHAST, EntityGhast.class);
        register(MobType.CREEPER, EntityCreeper.class);
        register(MobType.SLIME, EntitySlime.class);

        //mobs
        register(MobType.PIG, EntityPig.class);
        register(MobType.COW, EntityCow.class);
        register(MobType.SHEEP, EntitySheep.class);
        register(MobType.CHICKEN, EntityChicken.class);
        register(MobType.SQUID, EntitySquid.class);
        register(MobType.WOLF, EntityWolf.class);
    }

    private void register(MobType type, Class<? extends Entity> entityClazz) {
        entities.put(type, entityClazz);
    }

    public Class<? extends Entity> getEntityFromMobType(MobType type) {
        return entities.getOrDefault(type, DummyEntity.class);
    }
}
