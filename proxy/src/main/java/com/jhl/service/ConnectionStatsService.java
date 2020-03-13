package com.jhl.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jhl.utils.SynchronizedInternerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 提供账号连接数支持
 */
@Component
@Slf4j
public class ConnectionStatsService {
    // private final ConcurrentHashMap<Object, AtomicInteger> ACCOUNT_CONNECTION_COUNT_STATS = new ConcurrentHashMap<>(5);

    private final Cache<Object, AtomicInteger> ACCOUNT_CONNECTION_COUNT_STATS = CacheBuilder.newBuilder()
            .expireAfterWrite(65, TimeUnit.MINUTES).build();

    /**
     * @param accountId
     * @return
     */
    public int incrementAndGet(Object accountId) {
        Assert.notNull(accountId, "accountId must not be null");
        //存在
        AtomicInteger connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
        if (connectionCounter != null) {
            return connectionCounter.incrementAndGet();

        }
        //不存在
        synchronized (SynchronizedInternerUtils.getInterner().intern(accountId + ":acquireConnectionStats")) {

            connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
            if (connectionCounter != null) {
                return connectionCounter.get();
            }
            connectionCounter = new AtomicInteger(1);
            ACCOUNT_CONNECTION_COUNT_STATS.put(accountId, connectionCounter);
            return connectionCounter.get();
        }


    }

    public int get(Object accountId) {
        Assert.notNull(accountId, "accountId must not be null");
        AtomicInteger atomicInteger = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
        return atomicInteger == null ? 0 : atomicInteger.get();
    }

    public void delete(Object accountId) {
        Assert.notNull(accountId, "accountId must not be null");
        ACCOUNT_CONNECTION_COUNT_STATS.invalidate(accountId);
        log.info("connectionCounter  size:{}", ACCOUNT_CONNECTION_COUNT_STATS.size());

    }

    public int decrementAndGet(Object accountId) {
        if (accountId == null) return -1;

        AtomicInteger connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);

        if (connectionCounter != null)
            return connectionCounter.decrementAndGet();
        else
            return -1;
    }
}
