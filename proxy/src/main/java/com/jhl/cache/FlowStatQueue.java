package com.jhl.cache;

import com.jhl.config.ManagerConfig;
import com.ljh.common.model.FlowStat;
import com.ljh.common.model.ProxyAccount;
import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 运行时保持
 */
@Slf4j
@Component
public class FlowStatQueue {
    @Autowired
    ManagerConfig managerConfig;
    private static LinkedBlockingQueue<FlowStat> FS_QUEUE = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<FlowStat> FAILED_FS_QUEUE = new LinkedBlockingQueue<>();
    @Autowired
    RestTemplate restTemplate;

    public static void addQueue(FlowStat flowStat) {
        if (flowStat == null) return;
        FS_QUEUE.offer(flowStat);
    }


    public void runFailedQueue() {
        while (true) {
            boolean execResult = false;
            int failureTimes = 1;
            FlowStat take = null;
            try {
                take = FAILED_FS_QUEUE.take();
                failureTimes = take.getFailureTimes();
                if (failureTimes > 50) {
                    log.warn("上报流量失败超过10次");
                    //超过放弃
                    execResult = true;
                    continue;
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<ProxyAccount> entity = new HttpEntity(take, headers);

                ResponseEntity<Result> resultEt = restTemplate.postForEntity(managerConfig.getReportFlowUrl(), entity, Result.class);
                if (!resultEt.getStatusCode().is2xxSuccessful()) {
                    log.warn("上报流量失败:{}", resultEt);
                    tryAgain(take);
                    continue;
                } else {
                    Result result = resultEt.getBody();

                    if (result.getCode() != 200) { //系统内部错误
                        log.error("上报流量失败：{},{},error:{}", managerConfig.getReportFlowUrl(), take, result);
                        tryAgain(take);
                        continue;


                    }
                    //上报成功
                    execResult = true;
                }


            } catch (RestClientException e) { //网络错误
                log.error("上报流量失败：{}", e.getLocalizedMessage());
                tryAgain(take);


            } catch (InterruptedException e) {
                log.warn(" runFailedQueue InterruptedException：", e);
                //如果是系统关闭事件，并且size<1 ?
                if (FAILED_FS_QUEUE.size() < 1) {
                    break;
                } else {
                    log.error("失败上报流量队列还有数据，但是系统发出了中断指令。中断。。。");
                }

            } catch (Exception e) {
                log.error("runFailedQueue Exception error :{}", e);
                if (take != null) tryAgain(take);

            } finally {
                if (!execResult) {
                    try {
                        //0 6s 12s 18s 24s ...1min ....6min
                        int sleepTime = 6000 * failureTimes;
                        log.warn("上报失败 等待：{}ms",sleepTime);
                        Thread.sleep(sleepTime);
                    } catch (Exception e) {
                        log.error("runFailedQueue finally error:{} ", e);
                    }
                }

            }

        }
    }

    @PostConstruct
    public void start() {
        //todo  load local file
        new Thread(() -> {
            startFSQueue();
        }, "上报线程").start();
        new Thread(() -> {
            runFailedQueue();
        }, "失败上报线程").start();
    }

    private void startFSQueue() {
        while (true) {
            FlowStat take = null;
            try {
                take = FS_QUEUE.take();

               /* if (take.getFailureTimes() > 10) {

                    log.warn("上报流量失败超过10次");
                    continue;
                }*/

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<ProxyAccount> entity = new HttpEntity(take, headers);
                ResponseEntity<Result> resultEt = restTemplate.postForEntity(managerConfig.getReportFlowUrl(), entity, Result.class);
                if (!resultEt.getStatusCode().is2xxSuccessful()) {
                    log.warn("上报流量失败:{}", resultEt);
                    FAILED_FS_QUEUE.offer(take);
                    continue;
                } else {
                    Result result = resultEt.getBody();

                    if (result.getCode() != 200) { //系统内部错误
                        log.error("上报流量失败：{},{},error:{}", managerConfig.getReportFlowUrl(), take, result);
                        FAILED_FS_QUEUE.offer(take);
                        continue;


                    }
                }


            } catch (RestClientException e) { //网络错误
                log.error("上报流量失败：{}", e.getLocalizedMessage());
                FAILED_FS_QUEUE.offer(take);
                continue;

            } catch (InterruptedException e) {
                log.warn("InterruptedException：", e);
                //如果是系统关闭事件，并且size<1 ?
                if (FS_QUEUE.size() < 1) {
                    break;
                } else {
                    log.error("上报流量队列还有数据，但是系统发出了中断指令。忽略中断。。。");
                }

            } catch (Exception e) {
                log.error("FS_QUEUE error :{}", e);
                FAILED_FS_QUEUE.offer(take);
            }

        }
    }

    private void tryAgain(FlowStat take) {
        take.setFailureTimes(take.getFailureTimes() + 1);
        FAILED_FS_QUEUE.offer(take);
    }

    @PreDestroy
    public void destroy() {
        //todo

        /*  int i=0;
         while ( i< 10){
              if (FS_QUEUE.size()>0){


              }
         }*/
    }
}
