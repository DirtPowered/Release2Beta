package com.github.dirtpowered.releasetobeta.data.entity;

import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EntityCache {
    private Map<Integer, Entity> entities = new HashMap<>();

    public void addEntity(int entityId, Entity entity) {
        entities.put(entityId, entity);
    }

    public Entity getEntityById(int entityId) {
        return entities.get(entityId);
    }

    public void removeEntity(int entityId) {
        entities.remove(entityId);
    }
}
