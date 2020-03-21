package com.jhl.admin.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jhl.admin.entity.ConnectionStat;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ConnectionStatCache {
    Cache<String, ConnectionStat> cacheManager = CacheBuilder.newBuilder().
            maximumSize(1000).expireAfterAccess(5, TimeUnit.MINUTES).build();


    public void add(String accountNo, String host, int count) {
        ConnectionStat stat = cacheManager.getIfPresent(accountNo);
        if (stat != null) stat.createOrAdd(host, count);
        else {
            ConnectionStat connectionStat = new ConnectionStat();
            connectionStat.createOrAdd(host, count);
            cacheManager.put(accountNo, connectionStat);
        }

    }

    public  int getTotal(String accountNo){

        ConnectionStat stat = cacheManager.getIfPresent(accountNo);
        if (stat != null) return stat.getTotal();
        else return 0;
    }
}
