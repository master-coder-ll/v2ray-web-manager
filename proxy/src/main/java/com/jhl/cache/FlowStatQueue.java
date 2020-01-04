package com.jhl.cache;

import com.jhl.config.ManagerConfig;
import com.jhl.pojo.ComparableFlowStat;
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
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * 运行时保持
 */
@Slf4j
@Component
public class FlowStatQueue {
    @Autowired
    ManagerConfig managerConfig;

    private static DelayQueue<ComparableFlowStat> FS_QUEUE = new DelayQueue<>();
    @Autowired
    RestTemplate restTemplate;

    public static void addQueue(ComparableFlowStat flowStat) {
        if (flowStat == null) return;
        FS_QUEUE.offer(flowStat);
    }


    @PostConstruct
    public void start() {
        //todo  load local file
        new Thread(() -> {
            startFSQueue();
        }, "上报线程").start();

    }

    private void startFSQueue() {
        while (true) {
            ComparableFlowStat take = null;
            try {
                take = FS_QUEUE.take();
                //超过最大努力值不继续
                if (take.getFailureTimes() > 80) {
                    log.warn("上报次数大于最大值:{}", take);
                    continue;
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<ProxyAccount> entity = new HttpEntity(take, headers);
                ResponseEntity<Result> resultEt = restTemplate.postForEntity(managerConfig.getReportFlowUrl(), entity, Result.class);
                if (!resultEt.getStatusCode().is2xxSuccessful()) {
                    log.warn("上报流量失败:{}", resultEt);
                    tryAgain(take);
                } else {
                    Result result = resultEt.getBody();

                    if (result.getCode() != 200) { //系统内部错误
                        log.error("上报流量失败：{},{},error:{}", managerConfig.getReportFlowUrl(), take, result);
                        tryAgain(take);

                    }else {
                        log.info("上报流量成功");
                    }
                }


            } catch (RestClientException e) { //网络错误
                log.error("上报流量失败：{}", e.getLocalizedMessage());
                tryAgain(take);

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
                tryAgain(take);
            }

        }
    }

    private void tryAgain(ComparableFlowStat take) {
        take.setFailureTimes(take.getFailureTimes() + 1);

        // 6s 12s 18s 24s ...1min ....6min
        int sleepTime = 6000 * take.getFailureTimes();

        take.setNextTime(System.currentTimeMillis()+sleepTime);
        log.warn("上报失败 等待：{}ms,{}", sleepTime, FS_QUEUE.offer(take));
    }

    @PreDestroy
    public void destroy() throws InterruptedException {


        int i=0;
         while ( i< 10){
              if (FS_QUEUE.size()>0){
                  TimeUnit.SECONDS.sleep(1l);
                  i++;
              }else {
                  break;
              }
         }
    }

//    public static void main(String[] args) throws InterruptedException {
  //      DelayQueue<ComparableFlowStat> queue = new DelayQueue();
    //}
}
