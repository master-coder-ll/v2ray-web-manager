package com.jhl.admin.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jhl.admin.model.User;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * unSafe
 */
@Component
public class UserCache extends RootCache<String, User> {
    Cache<String, Integer> cacheManager = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(12, TimeUnit.HOURS).build();
    //expireAfterAccess
    Cache<Integer, User>  userCacheManager = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(12, TimeUnit.HOURS).build();

    @Override
    public User getCache(String key) {
        if (key ==null) return null;
         Integer id = cacheManager.getIfPresent(key);
         if (id !=null) return  userCacheManager.getIfPresent(id);
         return null;
    }

    @Override
    public void setCache(String key, User value) {
        Integer id = value.getId();
        cacheManager.put(key,id);
        userCacheManager.put(id,value);
    }

    @Override
    public void rmCache(String key) {
        cacheManager.invalidate(key);
    }
}
