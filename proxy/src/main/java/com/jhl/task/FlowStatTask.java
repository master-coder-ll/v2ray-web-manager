package com.jhl.task;

import com.jhl.constant.ManagerConstant;
import com.jhl.task.inteface.AbstractTask;
import com.ljh.common.model.FlowStat;
import com.ljh.common.model.ProxyAccount;
import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class FlowStatTask extends AbstractTask {
    private FlowStat flowStat;

    private ResponseEntity<Result> responseEntity = null;

    public FlowStatTask(FlowStat flowStat) {
        this.flowStat = flowStat;
    }

    @Override
    public void beforeRun() {
    }

    @Override
    public void runTask(RestTemplate restTemplate, ManagerConstant managerConstant) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProxyAccount> entity = new HttpEntity(flowStat, headers);
        responseEntity = restTemplate.postForEntity(managerConstant.getReportFlowUrl(), entity, Result.class);

    }

    @Override
    public void done() {
        if (responseEntity == null) {
            return;
        }
        ResponseEntity<Result> resultEt = responseEntity;
        if (!resultEt.getStatusCode().is2xxSuccessful()) {
            log.warn("上报失败:{}", resultEt);
            tryAgain(getTaskCondition());
        } else {
            Result resultEtBody = resultEt.getBody();

            if (resultEtBody.getCode() != 200) { //系统内部错误
                log.error("上报失败：{},{},error:{}", flowStat, responseEntity);
                tryAgain(getTaskCondition());

            }/* else {
                log.info("上报成功");
            }*/
        }
    }

    @Override
    public void catchException(Exception e) {
        tryAgain(getTaskCondition());
    }

    @Override
    public void setCondition(TaskCondition taskCondition) {
        taskCondition.setFailureTimes(0);
        taskCondition.setInterval(6000);
        taskCondition.setMaxFailureTimes(20);
    }


}
