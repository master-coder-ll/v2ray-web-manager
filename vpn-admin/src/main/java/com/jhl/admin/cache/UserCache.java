package com.jhl.admin.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jhl.admin.VO.UserVO;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * unSafe
 */
@Component
public class UserCache extends RootCache<String, UserVO> {
    Cache<String, Integer> cacheManager = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(6, TimeUnit.HOURS).build();
    //expireAfterAccess
    Cache<Integer, UserVO>  userCacheManager = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(6, TimeUnit.HOURS).build();

    @Override
    public UserVO getCache(String key) {
        if (key ==null) return null;
         Integer id = cacheManager.getIfPresent(key);
         if (id !=null) return  userCacheManager.getIfPresent(id);
         return null;
    }

    public void setCache(String key, UserVO value) {
        Integer id = value.getId();
        cacheManager.put(key,id);
        userCacheManager.put(id,value);
    }

    @Override
    public void rmCache(String key) {
        cacheManager.invalidate(key);
    }
}
