package com.jhl.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jhl.pojo.ConnectionStat;
import com.jhl.task.GlobalConnectionStatTask;
import com.jhl.task.service.TaskService;
import com.jhl.utils.SynchronizedInternerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

import static com.jhl.service.ProxyAccountService.ACCOUNT_EXPIRE_TIME;

/**
 * 提供账号连接数支持
 *
 *   全局异步同步数据。 最大程度保证正确，允许脏读。
 *  仅保证最终一致性
 *
 */
@Slf4j
public class ConnectionStatsCache {

    private static final Cache<Object, ConnectionStat> ACCOUNT_CONNECTION_COUNT_STATS = CacheBuilder.newBuilder()
            .expireAfterAccess(ACCOUNT_EXPIRE_TIME + 5, TimeUnit.MINUTES).build();

    public final static long _1HOUR_MS = 3600000;

    /**
     * @param accountId
     * @return
     */
    public static int incrementAndGet(String accountId) {
        Assert.notNull(accountId, "accountId must not be null");
        //存在
        ConnectionStat connectionStat = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
        if (connectionStat != null)
            return connectionStat.addAndGet(1);

        //不存在
        synchronized (SynchronizedInternerUtils.getInterner().intern(accountId + ":acquireConnectionStats")) {

            connectionStat = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
            if (connectionStat != null) {
                return connectionStat.addAndGet(1);
            }
            connectionStat = new ConnectionStat();
            ACCOUNT_CONNECTION_COUNT_STATS.put(accountId, connectionStat);
            return connectionStat.get();
        }


    }

    public static int get(String accountId) {
        Assert.notNull(accountId, "accountId must not be null");
        ConnectionStat connectionStat = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
        return connectionStat == null ? 0 : connectionStat.get();
    }

  /*  public void delete(String accountId) {
        Assert.notNull(accountId, "accountId must not be null");
        ACCOUNT_CONNECTION_COUNT_STATS.invalidate(accountId);
        // log.info("connectionCounter  size:{}", ACCOUNT_CONNECTION_COUNT_STATS.size());

    }*/

    public static Long getSize() {
        return ACCOUNT_CONNECTION_COUNT_STATS.size();
    }

    public static int decrementAndGet(Object accountId) {
        if (accountId == null) return -1;
        ConnectionStat connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);

        if (connectionCounter != null)
            return connectionCounter.addAndGet(-1);
        else
            return -1;
    }

    public static boolean isFull(String accountId, int maxConnections) {
        ConnectionStat connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
        if (connectionCounter != null) {
            return connectionCounter.isFull(maxConnections);
        }
        return false;
    }

    public static boolean canReport(String accountId) {
        ConnectionStat connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
        if (connectionCounter != null) {
            long interruptionTime = connectionCounter.getInterruptionTime();
            //操作一个小时后，可以继续执行上报
            //interruptionTime =0 ok
            // interruptionTime !=0 ok
            if ((System.currentTimeMillis() - interruptionTime) > _1HOUR_MS) {
                // 设置最新的上报时间
                connectionCounter.setInterruptionTime(System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    /**
     * 更新 全局连接数
     * @param accountNo
     * @param count
     */
    public static void updateGlobalConnectionStat(String accountNo, int count) {
        ConnectionStat connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountNo);
        if (connectionCounter != null) {
            connectionCounter.updateRemoteConnectionNum(count);
        }
    }

    /**
     * 上报当前账号在这台服务器的连接数
     */
    private  final  static  long _1MINUTES=60_000;

    /**
     * unSafe
     * @param accountNo
     * @param proxyIp
     */
    public  static void reportConnectionNum(String accountNo, String proxyIp){
        ConnectionStat connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountNo);
        int internalConnectionCount = connectionCounter.getInternalCount();

        if (System.currentTimeMillis()-connectionCounter.getLastReportTime()>_1MINUTES){
            GlobalConnectionStatTask globalConnectionStatTask =
                    new GlobalConnectionStatTask(accountNo,proxyIp,internalConnectionCount);
            TaskService.addTask(globalConnectionStatTask);
            //更新上报
            connectionCounter.setLastReportNum(internalConnectionCount);
            connectionCounter.setLastReportTime(System.currentTimeMillis());
            log.debug("上报提交任务。。。");
        }
    }
}
