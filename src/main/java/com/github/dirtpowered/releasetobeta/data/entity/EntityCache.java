package com.github.dirtpowered.releasetobeta.data.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityCache {

    private Map<Integer, Entity> entities = new HashMap<>();

    public EntityCache() {

    }

    public void addEntity(int entityId, Entity entity) {
        entities.put(entityId, entity);
    }

    public Map<Integer, Entity> getEntities() {
        return entities;
    }

    public Entity getEntityById(int entityId) {
        return entities.get(entityId);
    }

    public void removeEntity(int entityId) {
        entities.remove(entityId);
    }
}
