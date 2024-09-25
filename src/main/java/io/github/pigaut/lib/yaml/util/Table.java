package io.github.pigaut.lib.yaml.util;

import java.util.*;

public class Table<K1, K2, V> {
    private final Map<BiKey<K1, K2>, V> map = new HashMap<>();

    public Table() {}

    public void put(K1 key1, K2 key2, V value) {
        map.put(new BiKey<>(key1, key2), value);
    }

    public V get(K1 key1, K2 key2) {
        return map.get(new BiKey<>(key1, key2));
    }

    public V getOrDefault(K1 key1, K2 key2, V def) {
        return map.getOrDefault(new BiKey<>(key1, key2), def);
    }

    public V remove(K1 key1, K2 key2) {
        return map.remove(new BiKey<>(key1, key2));
    }

    public boolean containsKey(K1 key1, K2 key2) {
        return map.containsKey(new BiKey<>(key1, key2));
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int size() {
        return map.size();
    }

    public void clear() {
        map.clear();
    }

    private static class BiKey<K1, K2> {
        private final K1 key1;
        private final K2 key2;

        public BiKey(K1 key1, K2 key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        public K1 getKey1() {
            return key1;
        }

        public K2 getKey2() {
            return key2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BiKey<?, ?> biKey = (BiKey<?, ?>) o;
            return Objects.equals(key1, biKey.key1) && Objects.equals(key2, biKey.key2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key1, key2);
        }
    }
}

