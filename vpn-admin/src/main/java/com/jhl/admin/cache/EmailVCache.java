package com.jhl.admin.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 内置缓存
 */
@Component
public class EmailVCache extends RootCache {
    //10分钟过期
    Cache<Object, Object> cacheManager = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(10, TimeUnit.MINUTES).build();
    @Override
    public Object getCache(Object key) {


        return cacheManager.getIfPresent(key);
    }

    @Override
    public void setCache(Object key, Object value) {
        cacheManager.put(key, value);
    }


    @Override
    public void rmCache(Object key) {
         cacheManager.
                 invalidate(key);
    }
}
