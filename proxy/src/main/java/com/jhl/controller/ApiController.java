package com.jhl.controller;

import com.jhl.service.ProxyAccountService;
import com.jhl.v2ray.service.V2rayService;
import com.ljh.common.model.ProxyAccount;
import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/proxyApi")
public class ApiController {
    @Autowired
    V2rayService v2rayService;
    @Autowired
    ProxyAccountService proxyAccountService;


    @ResponseBody
    @PostMapping(value = "/account/del")
    public Result rmAccount(@RequestBody ProxyAccount proxyAccount) {
        try {
            if (proxyAccount == null) return Result.builder().code(405).message("accountNo 为空").build();
            String accountNo = proxyAccount.getAccountNo();
            proxyAccountService.rmProxyAccountCache(accountNo,proxyAccount.getHost());
            v2rayService.rmProxyAccount(proxyAccount.getV2rayHost(), proxyAccount.getV2rayManagerPort(), proxyAccount);
        } catch (Exception e) {
            log.error("rmAccount error :{}", e.getLocalizedMessage());
            return Result.builder().code(500).message(e.getMessage()).build();
        }
        return Result.SUCCESS();

    }

    /*@ResponseBody
    @PostMapping(value = "/account")
    public Result addAccount(@RequestBody ProxyAccount proxyAccount) {
        try {
            if (proxyAccount == null) return Result.builder().code(400).message("参数错误req:ProxyAccount").build();
            v2rayService.addProxyAccount(proxyAccount.getV2rayHost(), proxyAccount.getV2rayManagerPort(), proxyAccount);
            proxyAccountCache.addOrUpdate(proxyAccount);
        } catch (Exception e) {
            log.error("addAccount error :{}", e.getLocalizedMessage());
            return Result.builder().code(500).message(e.getMessage()).build();
        }

        return Result.SUCCESS();
    }*/

//    @Deprecated
//    @ResponseBody
//    @GetMapping(value = "/connection/{accountNo}")
//    public Result getConnection(@PathVariable String accountNo) {
//        try {
//
//            Result success = Result.SUCCESS();
//            success.setObj(trafficControllerService.getChannelCount(accountNo));
//            return success;
//        } catch (Exception e) {
//            log.error("getConnection error :{}", e.getLocalizedMessage());
//            return Result.builder().code(500).message(e.getMessage()).build();
//        }
//    }

/*
    @PostConstruct
    public void init() {

        try {
            Result result = restTemplate.getForEntity(address + "/init/proxyAccount", Result.class).getBody();
            if (result == null || result.getCode() != 200) {
                log.error("init 获取的数据有误：{}", result);
            }
            List<ProxyAccount> proxyAccounts = (List<ProxyAccount>) result.getObj();
            proxyAccounts.forEach(proxyAccount -> {
                v2rayService.addProxyAccount(proxyAccount.getTargetHost(), proxyAccount.getTargetPort(), proxyAccount);
                ProxyAccountCache.addOrUpdate(proxyAccount);
            });
        } catch (Exception e) {

            log.error(" error :{}", e);

        }

    }*/


   /* @ResponseBody
    @GetMapping(value = "/flowStat")
    @Deprecated
    public Result flowStat() {

        try {
            ArrayList<FlowStat> flowStats = Lists.newArrayList();
            ConcurrentHashMap<Integer, AtomicLong> statMap = FlowStatCache.getMap();
            statMap.forEach((accountId, used) -> {
                flowStats.add(new FlowStat(accountId, used.get()));
                FlowStatCache.rm(accountId);
            });
            return Result.builder().code(200).obj(flowStats).build();
        } catch (Exception e) {

            log.error("rmAccount error :{}", e);
            return Result.builder().code(500).message(e.getMessage()).build();
        }
    }*/
}
