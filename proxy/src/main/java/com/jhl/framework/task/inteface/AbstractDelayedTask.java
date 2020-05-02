package com.jhl.framework.task.inteface;

import com.alibaba.fastjson.JSON;
import com.jhl.common.constant.ManagerConstant;
import com.jhl.framework.task.TaskCondition;
import com.jhl.framework.task.service.MonitorService;
import com.jhl.framework.task.service.TaskService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 类 actor 模型, 每次执行通过邮箱queue提交AbstractTask的实现，
 * 每个task 内部不存在竞态条件。
 * 支持重新运行，支持无限运行 -> setNextRunTime() {@link MonitorService#init()}
 * 生命周期： beforeRun -》runTask ->done->catchException
 */
@Slf4j
public abstract class AbstractDelayedTask implements Delayed {

    protected String taskName;

    @Setter
    private  Long nextRunTime =System.currentTimeMillis();
    @Setter
    @Getter
    protected TaskCondition taskCondition = TaskCondition.builder().build();




    @Override
    public long getDelay(TimeUnit unit) {

        return unit.convert(nextRunTime-System.currentTimeMillis(), TimeUnit.MILLISECONDS);

    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.nextRunTime - ((AbstractDelayedTask) o).nextRunTime);
    }


    public abstract void beforeRun();

    public abstract void runTask(RestTemplate restTemplate, ManagerConstant managerConstant);

    public abstract void done();


    public void attachCondition() {
        TaskCondition taskCondition = getTaskCondition();
        if (taskCondition == null) taskCondition = TaskCondition.builder().build();
        setCondition(taskCondition);
    }

    /**
     * 排除 InterruptedException
     *
     */
    public abstract void catchException(Exception e);


    public abstract void setCondition(TaskCondition taskCondition);

    /**
     * 重试
     *
     */
    public void tryAgain(TaskCondition condition) {
        //如果是无限运行的直接跳过
        if (condition.getMaxFailureTimes() > 0) {
            //如果已经超过了重试次数
            if (condition.getFailureTimes() > condition.getMaxFailureTimes()) return;

            condition.setFailureTimes(condition.getFailureTimes() + 1);
        }
        setNextRunTime(System.currentTimeMillis() + condition.computeDelay());

        log.debug("任务:{},content:{}，将再次执行", this.taskName, JSON.toJSONString(this));
        TaskService.addTask(this);
    }

}
