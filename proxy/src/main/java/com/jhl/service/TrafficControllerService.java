package com.jhl.service;

import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TrafficControllerService 提供流量控制，
 * 每个账号持有生命周期内全局的  {@link GlobalTrafficShapingHandler}
 * todo 分布式流量控制 -never-分布式协调？
 */
@Component
public class TrafficControllerService {

    private ConcurrentHashMap<Object, GlobalTrafficShapingHandler> globalTrafficShapingHandlerMap = new ConcurrentHashMap<>(5);
    private ConcurrentHashMap<Object, AtomicInteger> channelTrafficCounter = new ConcurrentHashMap<>(5);


    public GlobalTrafficShapingHandler putIfAbsent(Object accountId, ScheduledExecutorService executor, Long readLimit, Long writeLimit) {
        if (accountId == null) throw new NullPointerException("accountId is null");
        if (globalTrafficShapingHandlerMap.containsKey(accountId)) return globalTrafficShapingHandlerMap.get(accountId);


        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(executor, writeLimit, readLimit);
        //* @return the previous value associated with {@code key}, or
        //      *         {@code null} if there was no mapping for {@code key}
        if (globalTrafficShapingHandlerMap.putIfAbsent(accountId, globalTrafficShapingHandler) != null) {
            //释放新建的
            globalTrafficShapingHandler.release();
            return globalTrafficShapingHandlerMap.get(accountId);
        }

        return globalTrafficShapingHandler;

    }

    public GlobalTrafficShapingHandler getGlobalTrafficShapingHandler(Object accountId) {
        if (accountId == null) throw new NullPointerException("accountId is null");
        return globalTrafficShapingHandlerMap.get(accountId);
    }

    public int incrementChannelCount(Object accountId) {

        if (accountId == null) throw new NullPointerException("countId is null");
        //存在
        if (channelTrafficCounter.containsKey(accountId)) return channelTrafficCounter.get(accountId).incrementAndGet();
        //不存在
        AtomicInteger count = new AtomicInteger(1);
        AtomicInteger atomicInteger = channelTrafficCounter.putIfAbsent(accountId, count);
        if (atomicInteger != null) return atomicInteger.incrementAndGet();
        else return count.get();

    }

    public int getChannelCount(Object accountId) {
        if (accountId == null) throw new NullPointerException("countId is null");
        AtomicInteger atomicInteger = channelTrafficCounter.get(accountId);
        return atomicInteger == null ? 0 : atomicInteger.get();
    }

    public int decrementChannelCount(Object accountId) {
        if (accountId == null) return -1;
        if (channelTrafficCounter.containsKey(accountId)) return channelTrafficCounter.get(accountId).decrementAndGet();
        else
            return -1;
    }

    public void releaseGroupGlobalTrafficShapingHandler(Object accountId) {
        if (accountId == null) throw new NullPointerException("countId is null");
        AtomicInteger channelCounter = channelTrafficCounter.get(accountId);
        if (channelCounter == null) return;
        synchronized (channelCounter) {
            int count = channelCounter.get();
            if (count < 1) {
                GlobalTrafficShapingHandler globalTrafficShapingHandler = globalTrafficShapingHandlerMap.get(accountId);
                if (globalTrafficShapingHandler != null) {
                    globalTrafficShapingHandler.release();
                }

                globalTrafficShapingHandlerMap.remove(accountId);
                channelTrafficCounter.remove(accountId);
            }
        }
    }


}
