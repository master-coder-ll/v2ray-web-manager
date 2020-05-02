package com.jhl.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jhl.common.pojo.AccountConnectionStat;
import com.jhl.framework.task.GlobalConnectionStatTask;
import com.jhl.framework.task.service.TaskService;
import com.jhl.common.utils.SynchronousPoolUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * 提供账号连接数支持
 * <p>
 * 全局异步同步数据。 最大程度保证正确，允许脏读。
 * 仅保证最终一致性
 */
@Slf4j
public class ConnectionStatsCache {

    private static final Cache<Object, AccountConnectionStat> ACCOUNT_CONNECTION_COUNT_STATS = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES).build();

    public final static long _1HOUR_MS = 3600000;


    public static void incr(String accountId, String host) {
        Assert.notNull(accountId, "accountId must not be null");
        //存在
        AccountConnectionStat accountConnectionStat = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
        if (accountConnectionStat != null) {
            accountConnectionStat.addAndGet(1, host);
            return;
        }
        //不存在
        synchronized (SynchronousPoolUtils.getWeakReference(accountId + ":connection:" + host)) {

            accountConnectionStat = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
            if (accountConnectionStat != null) {
                accountConnectionStat.addAndGet(1, host);
            } else {
                accountConnectionStat = new AccountConnectionStat();
                accountConnectionStat.addAndGet(1, host);
                ACCOUNT_CONNECTION_COUNT_STATS.put(accountId, accountConnectionStat);
            }

        }


    }

    public static int getByGlobal(String accountId) {
        Assert.notNull(accountId, "accountId must not be null");
        AccountConnectionStat accountConnectionStat = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
        return accountConnectionStat == null ? 0 : accountConnectionStat.getByGlobal();
    }

    public static int getBySeverInternal(String accountId) {
        Assert.notNull(accountId, "accountId must not be null");
        AccountConnectionStat accountConnectionStat = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
        return accountConnectionStat == null ? 0 : accountConnectionStat.getByServer();
    }

    public static int getByHost(String accountId, String host) {
        Assert.notNull(accountId, "accountId must not be null");
        AccountConnectionStat accountConnectionStat = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
        return accountConnectionStat == null ? 0 : accountConnectionStat.getByHost(host);
    }

  /*  public void delete(String accountId) {
        Assert.notNull(accountId, "accountId must not be null");
        ACCOUNT_CONNECTION_COUNT_STATS.invalidate(accountId);
        // log.info("connectionCounter  size:{}", ACCOUNT_CONNECTION_COUNT_STATS.size());

    }*/

    /**
     * <p>Expired entries may be counted in {@link Cache#size}, but will never be visible to read or
     * write operations. Expired entries are cleaned up as part of the routine maintenance described
     * in the class javadoc.
     *
     * @return size
     */
    public static Long getSize() {
        return ACCOUNT_CONNECTION_COUNT_STATS.size();
    }

    public static void decrement(Object accountId, String host) {
        if (accountId == null) return;
        AccountConnectionStat connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
        if (connectionCounter != null)
            connectionCounter.addAndGet(-1, host);

    }

    public static boolean isFull(String accountId, int maxConnections) {
        AccountConnectionStat connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
        if (connectionCounter != null) {
            return connectionCounter.isFull(maxConnections);
        }
        return false;
    }

    public static boolean canReport(String accountId) {
        synchronized (SynchronousPoolUtils.getWeakReference(accountId)) {
            AccountConnectionStat connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountId);
            if (connectionCounter != null) {
                long interruptionTime = connectionCounter.getInterruptionTime();
                //操作一个小时后，可以继续执行上报
                //interruptionTime =0 ok
                // interruptionTime !=0 ok
                if ((System.currentTimeMillis() - interruptionTime) > _1HOUR_MS) {
                    connectionCounter.setInterruptionTime(System.currentTimeMillis());
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 更新 全局连接数
     *

     */
    public static void updateGlobalConnectionStat(String accountNo, int count, long interruptionTime) {
        AccountConnectionStat connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountNo);
        if (connectionCounter != null) {
            connectionCounter.updateRemoteConnectionNum(count);
            //全局控制
            if (interruptionTime > 0) connectionCounter.setInterruptionTime(interruptionTime);
        }else{
            throw  new NullPointerException("AccountConnectionStat is null");
        }

    }

    /**
     * 上报当前账号在这台服务器的连接数
     */
    private final static long _30S = 30_000;

    /**
     * unSafe
     *

     */
    public static void reportConnectionNum(String accountNo, String proxyIp) {
        AccountConnectionStat connectionCounter = ACCOUNT_CONNECTION_COUNT_STATS.getIfPresent(accountNo);


        if ( connectionCounter != null && System.currentTimeMillis() - connectionCounter.getLastReportTime() > _30S) {
            int internalConnectionCount = connectionCounter.getByServer();
            GlobalConnectionStatTask globalConnectionStatTask =
                    new GlobalConnectionStatTask(accountNo, proxyIp, internalConnectionCount);
            TaskService.addTask(globalConnectionStatTask);
            //更新上报
            connectionCounter.setLastReportNum(internalConnectionCount);
            connectionCounter.setLastReportTime(System.currentTimeMillis());
            log.debug("上报提交任务。。。");
        }
    }
}
