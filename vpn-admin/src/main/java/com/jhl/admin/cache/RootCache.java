package com.jhl.admin.cache;

public abstract class RootCache<K,V> implements CacheI<K,V> {
    @Override
    public V getCache(K key) {
        throw  new UnsupportedOperationException();
    }

    @Override
    public void setCache(K key, V value) {
        throw  new UnsupportedOperationException();
    }

    @Override
    public void rmCache(K key) {
        throw  new UnsupportedOperationException();
    }

    @Override
    public boolean containKey(K key) {
        throw  new UnsupportedOperationException();
    }
}
