package com.jhl.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class ConnectionLimitCache {

    private static final Cache<String, Object> CACHE = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(1, TimeUnit.HOURS).build();


    private static final byte bytes = 1;

    public static void put(String key) {
            CACHE.put(key, bytes);
    }

    public static boolean containKey(String key) {
        return CACHE.getIfPresent(key) == null ? false : true;
    }
}
