package com.jhl.task.service;

import com.jhl.constant.ManagerConstant;
import com.jhl.task.TaskCondition;
import com.jhl.task.inteface.AbstractTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;


/**
 *
 */
@Slf4j
@Component
public class TaskService<T extends AbstractTask> {
    @Autowired
    ManagerConstant managerConstant;

    private static DelayQueue<AbstractTask> REPORTER_QUEUE = new DelayQueue<>();
    @Autowired
    RestTemplate restTemplate;


    public static <T extends AbstractTask> void addTask(T t) {
        if (t == null) return;
        if (IS_SHUTDOWN) throw new IllegalStateException(" The task service is closed");
        t.attachCondition();
        REPORTER_QUEUE.offer(t);
    }


    private static Thread workerThread = null;
    private volatile static boolean IS_SHUTDOWN = false;

    @PostConstruct
    public void start() {
        workerThread = new Thread(() -> {
            startTask();
        }, "task thread");
        workerThread.start();

    }

    private void startTask() {
        while (true) {
            AbstractTask abstractTask = null;
            try {
                if (IS_SHUTDOWN && REPORTER_QUEUE.size() <= 0) {
                    break;
                }
                abstractTask = REPORTER_QUEUE.take();


                TaskCondition taskCondition = abstractTask.getTaskCondition();
                //超过最大努力值不继续
                if (taskCondition.getMaxFailureTimes() != -1
                        && taskCondition.getFailureTimes() > taskCondition.getMaxFailureTimes()) {
                    log.warn("重试次数达到最大值:{}", abstractTask);
                    continue;
                }
                abstractTask.beforeRun();

                abstractTask.runTask(restTemplate, managerConstant);

            } catch (InterruptedException e) {
                //ignore
            } catch (Exception e) {
                log.error("task thread error :{}", e);
                abstractTask.catchException(e);
            } catch (Error e) {
                log.error(e.getMessage());
                //跳过
            } finally {
                if (abstractTask != null) abstractTask.done();
            }

        }
    }


    public static void destroy() throws InterruptedException {

        IS_SHUTDOWN = true;
        workerThread.interrupt();
        Thread.sleep(100);
        if (REPORTER_QUEUE.size() > 0) {
            Thread.sleep(5000);
            REPORTER_QUEUE.clear();
            workerThread.interrupt();
        }
    }


    public int getQueueSize() {
        return REPORTER_QUEUE.size();
    }
//    public static void main(String[] args) throws InterruptedException {
    //      DelayQueue<ComparableFlowStat> queue = new DelayQueue();
    //}
}
