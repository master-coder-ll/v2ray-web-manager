package com.jhl.service;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  提供账号连接数支持
 */
@Component
public class ConnectionStatsService {
    private final ConcurrentHashMap<Object, AtomicInteger> ACCOUNT_CONNECTION_MAP = new ConcurrentHashMap<>(5);
    /**
     *
     * @param accountId
     * @return
     */
    public int incrementAndGet(Object accountId) {

        Assert.notNull(accountId,"accountId must not be null");
        //存在
        if (ACCOUNT_CONNECTION_MAP.containsKey(accountId)) return ACCOUNT_CONNECTION_MAP.get(accountId).incrementAndGet();
        //不存在
        AtomicInteger count = new AtomicInteger(1);
        AtomicInteger atomicInteger = ACCOUNT_CONNECTION_MAP.putIfAbsent(accountId, count);
        if (atomicInteger != null) return atomicInteger.incrementAndGet();
        else return count.get();

    }

    public int get(Object accountId) {
        Assert.notNull(accountId,"accountId must not be null");
        AtomicInteger atomicInteger = ACCOUNT_CONNECTION_MAP.get(accountId);
        return atomicInteger == null ? 0 : atomicInteger.get();
    }

    public  void delete(Object accountId){
        Assert.notNull(accountId,"accountId must not be null");
        ACCOUNT_CONNECTION_MAP.remove(accountId);
    }
    public int decrementAndGet(Object accountId) {
        if (accountId == null) return -1;
        if (ACCOUNT_CONNECTION_MAP.containsKey(accountId)) return ACCOUNT_CONNECTION_MAP.get(accountId).decrementAndGet();
        else
            return -1;
    }
}
