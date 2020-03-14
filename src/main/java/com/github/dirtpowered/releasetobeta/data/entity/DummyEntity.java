package com.github.dirtpowered.releasetobeta.data.entity;

import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;
import com.github.steveice10.packetlib.Session;

public class DummyEntity extends Entity {

    public DummyEntity(int entityId, MobType type) {
        super(entityId, type);
    }

    public DummyEntity(int entityId) {
        super(entityId);
    }

    @Override
    public void onSpawn(Session session) {

    }
}
