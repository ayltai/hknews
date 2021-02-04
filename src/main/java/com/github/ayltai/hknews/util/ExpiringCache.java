package com.github.ayltai.hknews.util;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.cache.Cache;

import lombok.RequiredArgsConstructor;

public final class ExpiringCache<K, V> implements Cache<K, V> {
    @RequiredArgsConstructor
    private static class Entry<V> {
        private final long timestamp;
        private final V    value;
    }

    private final long             millisUntilExpiration;
    private final Map<K, Entry<V>> entries = new HashMap<>();

    public ExpiringCache(final long millisUntilExpiration) {
        this.millisUntilExpiration = millisUntilExpiration;
    }

    @Override
    public synchronized V get(final K key) {
        final Entry<V> entry = this.entries.get(key);
        if (entry == null) return null;

        if (System.currentTimeMillis() - entry.timestamp > this.millisUntilExpiration) {
            this.entries.remove(key);

            return null;
        }

        return entry.value;
    }

    @Override
    public void put(final K key, final V value) {
        this.entries.put(key, new Entry<>(System.currentTimeMillis(), value));
    }
}
