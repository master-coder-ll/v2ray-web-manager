package com.jhl.task;

import com.google.common.collect.Maps;
import com.jhl.cache.ConnectionStatsCache;
import com.jhl.constant.ManagerConstant;
import com.jhl.task.inteface.AbstractTask;
import com.ljh.common.model.ProxyAccount;
import com.ljh.common.model.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

/**
 * 应该基于触发
 */
@Slf4j
@AllArgsConstructor
@Getter
public class GlobalConnectionStatTask extends AbstractTask {

    private String accountNo;
    //当前的主机
    private String host;

    private int count;


    @Override
    public void beforeRun() {

    }

    @Override
    public void runTask(RestTemplate restTemplate, ManagerConstant managerConstant) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProxyAccount> entity = new HttpEntity(this, headers);
        String url = managerConstant.getAddress() + managerConstant.getGlobalConnectionStatUrl();
        HashMap<String, Object> v = Maps.newHashMap();
        v.put("accountNo", accountNo);
        v.put("host", host);
        v.put("count", count);
        ResponseEntity<Result> responseEntity = restTemplate.getForEntity(url, Result.class, v);

        Result result = responseEntity.getBody();
        if (responseEntity.getStatusCode().is2xxSuccessful() && result.getCode() == 200) {
            Integer count = Integer.parseInt(result.getObj() + "");
            ConnectionStatsCache.updateGlobalConnectionStat(accountNo, count);
            log.debug("Report success!,");
        }
    }

    @Override
    public void done() {

    }

    @Override
    public void catchException(Exception e) {

    }

    @Override
    public void setCondition(TaskCondition taskCondition) {
        taskCondition.setInterval(0);
        //    taskCondition.setUntilTime(System.currentTimeMillis()+_1MINUTE*60);

    }


}
