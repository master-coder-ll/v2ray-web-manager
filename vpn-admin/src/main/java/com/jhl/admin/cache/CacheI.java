package com.jhl.admin.cache;

public interface CacheI<K,V> {
    V getCache(K key);

    void  setCache(K key ,V value);


    void rmCache(K key);

    boolean containKey(K key);
}
