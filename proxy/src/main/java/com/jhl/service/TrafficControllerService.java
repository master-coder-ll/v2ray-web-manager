package com.jhl.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jhl.cache.ProxyAccountCache;
import com.jhl.utils.SynchronizedInternerUtils;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TrafficControllerService 提供流量控制，
 * 每个账号持有生命周期内全局的  {@link GlobalTrafficShapingHandler}
 * todo 分布式流量控制
 */
@Component
@Slf4j
public class TrafficControllerService {

    private final Cache<Object, GlobalTrafficShapingHandler> ACCOUNT_TRAFFIC_HANDLER_MAP = CacheBuilder.newBuilder()
            .expireAfterWrite(65, TimeUnit.MINUTES)
            .build();

    @Autowired
    ConnectionStatsService connectionStatsService;
    @Autowired
    ProxyAccountCache proxyAccountCache;

    /**
     * 为每个账号增加或者获取旧的 {@GlobalTrafficShapingHandler}，
     *
     * @param accountId  accountId
     * @param executor   可定时的线程池
     * @param readLimit  读限制速度
     * @param writeLimit 写限制速度
     * @return
     */
    public GlobalTrafficShapingHandler putIfAbsent(Object accountId, ScheduledExecutorService executor, Long readLimit, Long writeLimit) {
        Assert.notNull(accountId, "accountId must not be null");

        GlobalTrafficShapingHandler trafficShapingHandler = ACCOUNT_TRAFFIC_HANDLER_MAP.getIfPresent(accountId);
        if (trafficShapingHandler != null) return trafficShapingHandler;

        synchronized (SynchronizedInternerUtils.getInterner().intern(accountId+":acquireGlobalTrafficShapingHandler")) {
            trafficShapingHandler= ACCOUNT_TRAFFIC_HANDLER_MAP.getIfPresent(accountId);
            if (trafficShapingHandler !=null) return trafficShapingHandler;
            trafficShapingHandler = new GlobalTrafficShapingHandler(executor, writeLimit, readLimit);
            ACCOUNT_TRAFFIC_HANDLER_MAP.put(accountId,trafficShapingHandler);
        }
        return trafficShapingHandler;
    }


    /**
     * 应该获取锁，但是不必要
     * @param accountId
     * @return
     */
    public GlobalTrafficShapingHandler getGlobalTrafficShapingHandler(Object accountId) {

        Assert.notNull(accountId, "accountId must not be null");
        return ACCOUNT_TRAFFIC_HANDLER_MAP.getIfPresent(accountId);
    }
    /**
     * 应该获取锁，但是不必要
     * @param accountId
     * @return
     */
    public void releaseGroupGlobalTrafficShapingHandler(Object accountId) {
        Assert.notNull(accountId, "accountId must not be null");
        int connections = connectionStatsService.get(accountId);
        if (connections < 1) {
            GlobalTrafficShapingHandler globalTrafficShapingHandler = ACCOUNT_TRAFFIC_HANDLER_MAP.getIfPresent(accountId);
            if (globalTrafficShapingHandler != null) {
                globalTrafficShapingHandler.release();
            }
            ACCOUNT_TRAFFIC_HANDLER_MAP.invalidate(accountId);
            log.info("TrafficShapingHandler cache size:{}", ACCOUNT_TRAFFIC_HANDLER_MAP.size());
        }
    }


}
