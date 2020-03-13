package com.github.dirtpowered.releasetobeta.data.entity;

import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;

public abstract class Entity {

    private int entityId;
    private MobType mobType;
    private boolean betaPlayer;

    public Entity(int entityId, MobType type) {
        this.entityId = entityId;
        this.mobType = type;
    }

    public Entity(int entityId, boolean isBetaPlayer) {
        this.entityId = entityId;
        this.betaPlayer = isBetaPlayer;
    }

    public boolean isBetaPlayer() {
        return betaPlayer;
    }

    public MobType getMobType() {
        return mobType;
    }

    public int getEntityId() {
        return entityId;
    }
}
