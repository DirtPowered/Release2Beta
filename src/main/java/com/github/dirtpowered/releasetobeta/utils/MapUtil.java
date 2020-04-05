package com.github.dirtpowered.releasetobeta.utils;

import java.util.List;
import java.util.stream.Collectors;

public class MapUtil {
    public static <K, V, Q extends K> List<V> transform(final List<Q> input, final java.util.function.Function<K, V> kvFunction) {
        if (null == input) {
            return null;
        }
        return input.stream().map(kvFunction).collect(Collectors.toList());
    }
}
