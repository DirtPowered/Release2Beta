package com.github.dirtpowered.releasetobeta.data.entity;

import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;

public abstract class Entity {

    private int entityId;
    private MobType mobType;

    Entity(int entityId, MobType type) {
        this.entityId = entityId;
        this.mobType = type;
    }

    public MobType getMobType() {
        return mobType;
    }

    public int getEntityId() {
        return entityId;
    }
}
