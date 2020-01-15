package com.jhl.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.jhl.config.ManagerConfig;
import com.jhl.utils.Utils;
import com.jhl.v2ray.service.V2rayService;
import com.ljh.common.model.ProxyAccount;
import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 运行时保持
 */
@Slf4j
@Component
public class ProxyAccountCache {

    @Autowired
    ManagerConfig managerConfig;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    V2rayService v2rayService;

    Cache<String, ProxyAccount> PA_MAP = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(1, TimeUnit.HOURS).build();
    //  CacheBuilder<String, Object> block_account =    CacheBuilder<String, Object>.CacheBuilder
    //accountno ,int
    Cache<String, AtomicInteger> REQ_COUNT = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(1, TimeUnit.MINUTES).build();


    public void addOrUpdate(ProxyAccount proxyAccount) {
        if (null == proxyAccount || proxyAccount.getAccountId() == null
        ) throw new NullPointerException("ProxyAccount is null");

        PA_MAP.put(proxyAccount.getAccountNo(), proxyAccount);
    }

    public ProxyAccount get(String accountNo) {
        ProxyAccount proxyAccount = PA_MAP.getIfPresent(accountNo);

        AtomicInteger reqCountObj = REQ_COUNT.getIfPresent(accountNo);
        int reqCount = reqCountObj == null ? 0 : reqCountObj.get();
        if (proxyAccount == null && reqCount < 3) {
            synchronized (Utils.getStringWeakReference(accountNo + ":getRemotePAccount")) {
                proxyAccount = PA_MAP.getIfPresent(accountNo);
                if (proxyAccount !=null) return  proxyAccount;
                //远程请求，获取信息
                proxyAccount = getRemotePAccount(accountNo);

                if (proxyAccount == null) {
                    AtomicInteger counter = REQ_COUNT.getIfPresent(accountNo);
                    synchronized (accountNo.intern()) {
                        if (counter != null) {
                            counter.addAndGet(1);
                        } else {
                            REQ_COUNT.put(accountNo, new AtomicInteger(1));
                        }
                    }
                } else {
                    //确保存在账号
                    addOrUpdate(proxyAccount);
                    try {
                        v2rayService.addProxyAccount(proxyAccount.getV2rayHost(), proxyAccount.getV2rayManagerPort(), proxyAccount);
                    } catch (Exception e) {
                        log.warn("增加失败:{}", e.getLocalizedMessage());
                    }


                }
            }
        }
        if (reqCount > 0) log.info("阻止远程请求:{}:{}", accountNo, reqCount);


        return proxyAccount;
    }


    private ProxyAccount getRemotePAccount(String accountNo) {
        log.info("getRemotePAccount:{}", accountNo);
        HashMap<String, Object> kvMap = Maps.newHashMap();
        kvMap.put("accountNo", accountNo);
        ResponseEntity<Result> entity = restTemplate.getForEntity(managerConfig.getGetProxyAccountUrl(),
                Result.class, kvMap);
        if (!entity.getStatusCode().is2xxSuccessful()) {
            log.error("获取pAccount 错误:{}", entity);
            return null;
        }
        Result result = entity.getBody();
        if (result.getCode() != 200) {
            log.warn("获取远程账号错误，可能admin端没启动，或者配置错误 error:{}", JSON.toJSONString(result));
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(result.getObj()), ProxyAccount.class);
    }

    public void rmProxyAccountCache(String accountNo) {
        PA_MAP.invalidate(accountNo);
    }

}
