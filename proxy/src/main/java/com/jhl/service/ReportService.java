package com.jhl.service;

import com.jhl.constant.ManagerConstant;
import com.jhl.pojo.ConnectionLimit;
import com.jhl.pojo.Report;
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
import java.util.concurrent.DelayQueue;


 /**
 * singleton
 * 主动push模式的队列
 */
@Slf4j
@Component
public class ReportService<T extends Report> {
    @Autowired
    ManagerConstant managerConstant;

    private static DelayQueue<Report> REPORT_QUEUE = new DelayQueue<>();
    @Autowired
    RestTemplate restTemplate;

    public static <T extends Report> void addQueue(T t) {
        if (t == null) return;
        if (IS_SHUTDOWN) throw  new IllegalStateException(" the report service is closed");
        REPORT_QUEUE.offer(t);
    }


    private static Thread workerThread = null;
    private   static  boolean IS_SHUTDOWN=false;
    @PostConstruct
    public void start() {
        workerThread = new Thread(() -> {
            startQueue();
        }, "report thread");
        workerThread.start();

    }

    private void startQueue() {
        while (true) {
            Report take = null;
            try {

                if (IS_SHUTDOWN && REPORT_QUEUE.size()<=0) break;

                take = REPORT_QUEUE.take();
                //超过最大努力值不继续
                if (take.getFailureTimes() > 80) {
                    log.warn("上报次数大于最大值:{}", take);
                    continue;
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                Object value = take.getT();

                ResponseEntity<Result> resultEt = null;

                if (value instanceof FlowStat) {

                    HttpEntity<ProxyAccount> entity = new HttpEntity(value, headers);
                    resultEt = restTemplate.postForEntity(managerConstant.getReportFlowUrl(), entity, Result.class);

                } else if (value instanceof ConnectionLimit) {
                    resultEt = restTemplate.getForEntity(managerConstant.getReportOverConnectionLimitUrl(),
                            Result.class, ((ConnectionLimit) value).getAccountNo());
                }else {
                    log.warn("不支持的上报类型");
                    continue;
                }


                if (resultEt == null) {
                    continue;
                }
                if (!resultEt.getStatusCode().is2xxSuccessful()) {
                    log.warn("上报失败:{}", resultEt);
                    tryAgain(take);
                } else {
                    Result result = resultEt.getBody();

                    if (result.getCode() != 200) { //系统内部错误
                        log.error("上报失败：{},{},error:{}", managerConstant.getReportFlowUrl(), take, result);
                        tryAgain(take);

                    } else {
                        log.info("上报成功");
                    }
                }


            } catch (RestClientException e) { //网络错误
                log.error("上报失败：{}", e.getLocalizedMessage());
                tryAgain(take);

            } catch (InterruptedException e) {
             /*   log.warn("InterruptedException：", e);
                //如果是系统关闭事件，并且size<1 ?
                if (REPORT_QUEUE.size() < 1) {
                    break;
                } else {
                    log.error("上报队列还有数据，但是系统发出了中断指令。忽略中断。。。");
                }
*/
            } catch (Exception e) {
                log.error("FS_QUEUE error :{}", e);
                tryAgain(take);
            }

        }
    }

    private void tryAgain(Report take) {
        take.setFailureTimes(take.getFailureTimes() + 1);

        // 6s 12s 18s 24s ...1min ....6min
        int sleepTime = 6000 * take.getFailureTimes();

        take.setNextTime(System.currentTimeMillis() + sleepTime);
        log.warn("上报失败 等待：{}ms,{}", sleepTime, REPORT_QUEUE.offer(take));
    }

    public static void destroy() throws InterruptedException {

        IS_SHUTDOWN=true;
        workerThread.interrupt();
        Thread.sleep(100);
        if (REPORT_QUEUE.size()>0) {
            Thread.sleep(5000);
            REPORT_QUEUE.clear();
            workerThread.interrupt();
        }
    }

//    public static void main(String[] args) throws InterruptedException {
    //      DelayQueue<ComparableFlowStat> queue = new DelayQueue();
    //}
}
