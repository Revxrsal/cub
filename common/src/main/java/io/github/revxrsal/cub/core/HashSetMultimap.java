package io.github.revxrsal.cub.core;

import java.util.*;
import java.util.Map.Entry;

public class HashSetMultimap<K, V> {

    private final Map<K, Set<V>> map = new HashMap<>();

    private Set<V> set(K key) {
        return map.computeIfAbsent(key, k -> new HashSet<>());
    }

    private Set<V> setOrEmpty(K key) {
        return map.getOrDefault(key, Collections.emptySet());
    }

    public boolean put(K key, V value) {
        return set(key).add(value);
    }

    public Set<V> removeAll(K key) {
        Set<V> set = map.remove(key);
        if (set == null) return Collections.emptySet();
        return set;
    }

    public Set<Entry<K, Set<V>>> entries() {
        return map.entrySet();
    }


}
