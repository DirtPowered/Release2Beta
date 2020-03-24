package com.github.dirtpowered.releasetobeta.data.mapping;

import java.util.HashMap;
import java.util.Map;

public abstract class Remapper<T> {

    private Map<Integer, T> registry = new HashMap<>();

    void remap(int from, T to) {
        registry.put(from, to);
    }

    public T getFromId(int id) {
        return registry.getOrDefault(id, null);
    }

    public boolean exist(int id) {
        return registry.containsKey(id);
    }
}
