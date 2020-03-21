package com.jhl.pojo;

import com.jhl.cache.ConnectionStatsCache;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步，延迟。 最大程度保证正确，允许脏读。
 */
public class AccountConnectionStat {
    //每个账号的连接数
    private AtomicInteger accountConnectionsCounter = new AtomicInteger(0);

    private ConcurrentHashMap<String, AtomicInteger> hostCounter = new ConcurrentHashMap<>(3);

    //远程全局连接数
    // unsafe
    private int remoteConnectionNum = 0;
    //上次上报的数量
    @Getter
    @Setter
    // unsafe
    private int lastReportNum = 0;
    //上次上报的时间
    @Getter
    @Setter
    private long lastReportTime = 0;
    //上次超过最大连接数上报时间
    @Getter
    private long interruptionTime = 0;

    public final static long _5MINUTE_MS = 5 * 60 * 1000;


    /**
     * 对 totalCounter 设置， 对 hostCounter 设置
     *
     * @param count
     */
    public void addAndGet(int count, String host) {
        int v = accountConnectionsCounter.addAndGet(count);
        if (v < 0) accountConnectionsCounter.set(0);
        //account --> host
        AtomicInteger hostCount = hostCounter.get(host);

        if (hostCount != null) {
            int i = hostCount.addAndGet(count);
            if (i < 0) {
                hostCounter.remove(host);
            }
        } else {
            if (count < 0) return;
            hostCount = new AtomicInteger(count);
            AtomicInteger old = hostCounter.putIfAbsent(host, hostCount);
            if (old != null) old.addAndGet(count);

        }
    }


    public boolean isFull(int maxConnections) {
        //interruptionTime 如果小于 1小时 true
        if (interruptionTime > 0 &&
                (System.currentTimeMillis() - interruptionTime) < ConnectionStatsCache._1HOUR_MS) {
            return true;
        }
        boolean result = false;
        int total = getByGlobal();
        if (total > maxConnections) result = true;

        return result;
    }

    /**
     * 设置屏蔽开始时间
     *
     * @param interruptionTime 屏蔽开始时间
     */
    public void setInterruptionTime(long interruptionTime) {
        this.interruptionTime = interruptionTime;
    }

    public void updateRemoteConnectionNum(int count) {
        if (count < 1) return;
        this.remoteConnectionNum = count;
    }

    /**
     * 跟以前全部服务器的账号总连接数
     *
     * @return
     */
    public int getByGlobal() {
        //小于5分钟内
        if (remoteConnectionNum > 0 && (System.currentTimeMillis() - lastReportTime) < _5MINUTE_MS) {
            int remote = (remoteConnectionNum - lastReportNum);
            return remote < 0 ? 0 : remote + getByServer();
        }
        return getByServer();
    }

    /**
     * 获取当前服务器的account连接数数量
     *
     * @return
     */
    public int getByServer() {
        return accountConnectionsCounter.get();
    }

    /**
     * 获取不同host的连接数
     *
     * @return
     */
    public int getByHost(String host) {
        AtomicInteger counter = hostCounter.get(host);
        return counter == null ? 0 : counter.get();
    }
}
