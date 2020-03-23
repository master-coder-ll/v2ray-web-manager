package com.jhl.admin.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jhl.admin.entity.ConnectionStat;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ConnectionStatCache {

    public static final long EXPIRE_TIME = 60;
    Cache<String, ConnectionStat> cacheManager = CacheBuilder.newBuilder().
            maximumSize(1000).expireAfterAccess(EXPIRE_TIME, TimeUnit.MINUTES).build();


    public void add(String accountNo, String host, int count) {
        ConnectionStat stat = cacheManager.getIfPresent(accountNo);
        if (stat != null) stat.createOrAdd(host, count);
        else {
            ConnectionStat connectionStat = new ConnectionStat();
            connectionStat.createOrAdd(host, count);
            cacheManager.put(accountNo, connectionStat);
        }

    }

    public int getTotal(String accountNo, int maxConnections) {

        ConnectionStat stat = cacheManager.getIfPresent(accountNo);
        Integer count = 0;
        if (stat != null) count = stat.getTotal();

        if (count > maxConnections) {
            stat.setLastBlock(System.currentTimeMillis());
        }
        return  count;
    }

        public long getLastBlackTime(String accountNo){
            ConnectionStat stat = cacheManager.getIfPresent(accountNo);
            return  stat.getLastBlock();
        }
}
