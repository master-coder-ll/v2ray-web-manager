package com.jhl.service;

import com.jhl.cache.ProxyAccountCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.management.ManagementFactoryHelper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class MonitorService {

    @Autowired
    ReporterService reporterService;

    @Autowired
    TrafficControllerService trafficControllerService;
    @Autowired
    ConnectionStatsService connectionStatsService;
    @Autowired
    ProxyAccountCache proxyAccountCache;

    private static  long  _1k=1024;

    private volatile ScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    public void init() {
        scheduledExecutorService = Executors.newScheduledThreadPool(1, new MonitorThreadFactory());
        scheduledExecutorService.scheduleAtFixedRate(
                () -> {
                    internalPollInfo();
                   // jvmMemoryInfo();

                }, 0, 60, TimeUnit.SECONDS
        );

    }

    @PreDestroy
    public void destroy() {
        scheduledExecutorService.shutdown();
    }

    /**
     * j9 无法执行
     */
    private void jvmMemoryInfo() {
   //     MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
      //  MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
     //   log.info("heap memory :{}", heapMemoryUsage.toString());
        ManagementFactoryHelper.getBufferPoolMXBeans().forEach(bufferPoolMXBean -> {
            log.info( bufferPoolMXBean.getName()+"pool,MemoryUsed:"+bufferPoolMXBean.getMemoryUsed()/_1k
                    +"k,TotalCapacity:"+bufferPoolMXBean.getTotalCapacity()/_1k+"k");
        });
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        memoryPoolMXBeans.forEach(pool ->{
            log.info( pool.getName()+":"+pool.getPeakUsage().toString());
                }

        );


    }


    private void internalPollInfo() {
        log.info("TrafficController:{},ReporterQueue:{},ConnectionPool:{},AccountCache:{}",
                trafficControllerService.getSize(), reporterService.getQueueSize(),
                connectionStatsService.getSize(), proxyAccountCache.getSize());
     /*   log.info("netty申请的物理内存,可能不准确:"+ PlatformDependent.directBufferPreferred()
                +PlatformDependent.usedDirectMemory());*/
    }

    private class MonitorThreadFactory implements ThreadFactory {

        private AtomicInteger counter = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "monitor-threadPool-" + counter.getAndIncrement());
        }
    }


}
