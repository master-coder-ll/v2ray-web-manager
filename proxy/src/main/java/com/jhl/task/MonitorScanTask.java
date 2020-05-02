package com.jhl.task;

import com.jhl.constant.ManagerConstant;
import com.jhl.service.MonitorService;
import com.jhl.task.inteface.AbstractDelayedTask;
import com.v2ray.core.common.log.Log;
import io.netty.buffer.PooledByteBufAllocator;
import org.springframework.web.client.RestTemplate;

/**
 * 每次监控
 */
public class MonitorScanTask extends AbstractDelayedTask {
    private MonitorService monitorService;

    public MonitorScanTask(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @Override
    public void beforeRun() {

    }

    @Override
    public void runTask(RestTemplate restTemplate, ManagerConstant managerConstant) {
        monitorService.internalPollInfo();
        //  monitorService.jvmMemoryInfo();

    }

    @Override
    public void done() {
        tryAgain(getTaskCondition());
    }

    @Override
    public void catchException(Exception e) {
        tryAgain(getTaskCondition());
    }

    @Override
    public void setCondition(TaskCondition taskCondition) {
        taskCondition.setMaxFailureTimes(-1);
        taskCondition.setFailureTimes(0);
        taskCondition.setInterval(60_000L * 5);
    }
}
