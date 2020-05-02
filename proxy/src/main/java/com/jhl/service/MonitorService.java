package com.jhl.service;

import com.jhl.cache.ConnectionStatsCache;
import com.jhl.cache.TrafficControllerCache;
import com.jhl.task.MonitorScanTask;
import com.jhl.task.service.TaskService;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.management.ManagementFactoryHelper;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class MonitorService {

    @Autowired
    TaskService taskService;

    @Autowired
    ProxyAccountService proxyAccountService;

    private static long _1k = 1024;

    @PostConstruct
    public void init() {
        TaskService.addTask(new MonitorScanTask(this));
    }


    /**
     * j9 无法执行
     */
    public void jvmMemoryInfo() {
        //     MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        //  MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        //   log.info("heap memory :{}", heapMemoryUsage.toString());
        ManagementFactoryHelper.getBufferPoolMXBeans().forEach(bufferPoolMXBean -> {
            log.info(bufferPoolMXBean.getName() + "pool,MemoryUsed:" + bufferPoolMXBean.getMemoryUsed() / _1k
                    + "k,TotalCapacity:" + bufferPoolMXBean.getTotalCapacity() / _1k + "k");
        });
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        memoryPoolMXBeans.forEach(pool -> {
                    log.info(pool.getName() + ":" + pool.getPeakUsage().toString());
                }

        );


    }

    public void internalPollInfo() {
        log.info("TrafficController:{},ReporterQueue:{},ConnectionPool:{},AccountCache:{}",
                TrafficControllerCache.getSize(), taskService.getQueueSize(),
                ConnectionStatsCache.getSize(), proxyAccountService.getSize());
        log.info("netty是否使用直接内存:" + PlatformDependent.directBufferPreferred() + ",使用量(-1为无法探知)B:"
                + PlatformDependent.usedDirectMemory());

        log.info("metric:{}", PooledByteBufAllocator.DEFAULT.metric().toString());
    }

    private class MonitorThreadFactory implements ThreadFactory {

        private AtomicInteger counter = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "monitor-threadPool-" + counter.getAndIncrement());
        }
    }


}
