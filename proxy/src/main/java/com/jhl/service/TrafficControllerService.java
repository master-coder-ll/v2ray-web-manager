package com.jhl.service;

import com.jhl.cache.ProxyAccountCache;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * TrafficControllerService 提供流量控制，
 * 每个账号持有生命周期内全局的  {@link GlobalTrafficShapingHandler}
 * todo 分布式流量控制
 */
@Component
public class TrafficControllerService {

    private final ConcurrentHashMap<Object, GlobalTrafficShapingHandler> ACCOUNT_TRAFFIC_HANDLER_MAP = new ConcurrentHashMap<>(5);

    @Autowired
    ConnectionStatsService connectionStatsService;
    @Autowired
    ProxyAccountCache proxyAccountCache;
    /**
     * 为每个账号增加或者获取旧的 {@GlobalTrafficShapingHandler}，
     *
     * @param accountId accountId
     * @param executor   可定时的线程池
     * @param readLimit   读限制速度
     * @param writeLimit   写限制速度
     * @return
     */
    public GlobalTrafficShapingHandler putIfAbsent(Object accountId, ScheduledExecutorService executor, Long readLimit, Long writeLimit) {
        Assert.notNull(accountId,"accountId must not be null");


        if (ACCOUNT_TRAFFIC_HANDLER_MAP.containsKey(accountId)) return ACCOUNT_TRAFFIC_HANDLER_MAP.get(accountId);


        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(executor, writeLimit, readLimit);
        //* @return the previous value associated with {@code key}, or
        //      *         {@code null} if there was no mapping for {@code key}
        if (ACCOUNT_TRAFFIC_HANDLER_MAP.putIfAbsent(accountId, globalTrafficShapingHandler) != null) {
            //释放新建的
            globalTrafficShapingHandler.release();
            return ACCOUNT_TRAFFIC_HANDLER_MAP.get(accountId);
        }

        return globalTrafficShapingHandler;

    }

    public GlobalTrafficShapingHandler getGlobalTrafficShapingHandler(Object accountId) {
       
        Assert.notNull(accountId,"accountId must not be null");
        return ACCOUNT_TRAFFIC_HANDLER_MAP.get(accountId);
    }

    public void releaseGroupGlobalTrafficShapingHandler(Object accountId) {
        Assert.notNull(accountId,"accountId must not be null");
        int connections = connectionStatsService.get(accountId);
        if (connections < 1) return;
        synchronized (accountId) {

            if (connections < 1) {
                GlobalTrafficShapingHandler globalTrafficShapingHandler = ACCOUNT_TRAFFIC_HANDLER_MAP.get(accountId);
                if (globalTrafficShapingHandler != null) {
                    globalTrafficShapingHandler.release();
                }

                ACCOUNT_TRAFFIC_HANDLER_MAP.remove(accountId);
            }
        }
    }


}
