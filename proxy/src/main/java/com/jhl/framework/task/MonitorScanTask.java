package com.jhl.framework.task;

import com.jhl.common.constant.ManagerConstant;
import com.jhl.framework.task.service.MonitorService;
import com.jhl.framework.task.inteface.AbstractDelayedTask;
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
