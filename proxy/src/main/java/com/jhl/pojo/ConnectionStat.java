package com.jhl.pojo;

import com.jhl.cache.ConnectionStatsCache;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步，延迟。 最大程度保证正确，允许脏读。
 */
public class ConnectionStat {
    //每个账号的连接数
    private AtomicInteger counter = new AtomicInteger(1);
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
     * @param count 返回全局的统计
     * @return
     */
    public int addAndGet(int count) {
        int v = counter.addAndGet(count);
        if (v < 0) counter.set(0);

        return get();
    }

    public int get() {
        //小于5分钟内
        if (remoteConnectionNum > 0 && (System.currentTimeMillis() - lastReportTime) < _5MINUTE_MS) {
            synchronized (this) {
                return remoteConnectionNum - lastReportNum + getInternalCount();
            }
        }
        return getInternalCount();
    }


    public boolean isFull(int maxConnections) {
        //interruptionTime 如果小于 1小时 true
        if (interruptionTime > 0 &&
                (System.currentTimeMillis() - interruptionTime) < ConnectionStatsCache._1HOUR_MS) {
            return true;
        }
        boolean result = false;
        int total = get();
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
     * 获取当前服务的连接数数量
     *
     * @return
     */
    public int getInternalCount() {
        return counter.get();

    }
}
