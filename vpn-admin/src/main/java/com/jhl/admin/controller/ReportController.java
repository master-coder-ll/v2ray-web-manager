package com.jhl.admin.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.jhl.admin.model.Account;
import com.jhl.admin.model.Server;
import com.jhl.admin.model.Stat;
import com.jhl.admin.repository.AccountRepository;
import com.jhl.admin.repository.ServerRepository;
import com.jhl.admin.repository.StatRepository;
import com.jhl.admin.service.AccountService;
import com.jhl.admin.service.StatService;
import com.jhl.admin.service.v2ray.ProxyEvent;
import com.jhl.admin.service.v2ray.ProxyEventService;
import com.jhl.admin.service.v2ray.V2RayProxyEvent;
import com.jhl.admin.util.Utils;
import com.ljh.common.model.FlowStat;
import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/report")
public class ReportController {

    @Autowired
    StatRepository statRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    StatService statService;
    @Autowired
    ServerRepository serverRepository;
    private long G = 1024 * 1024 * 1024;

    @Autowired
    AccountService accountService;
    @Autowired
    ProxyEventService proxyEventService;
    //幂等
    Cache<String, Object> cacheManager = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(1, TimeUnit.HOURS).build();

    private static Object object = new Object();

    //weak reference
    private final static Interner<String> STRING_WEAK_POLL = Interners.newWeakInterner();

    @ResponseBody
    @PostMapping("/flowStat")
    public Result flowStat(@RequestBody FlowStat flowStat) {


        if (flowStat == null) {
            log.error("收到空的  flowStat report。。");
            return Result.SUCCESS();
        }
        String uniqueId = flowStat.getUniqueId();

        if (cacheManager.getIfPresent(uniqueId) != null) {
            log.warn("重复的stat上报{}",uniqueId);
            return Result.SUCCESS();
        }
        synchronized (STRING_WEAK_POLL.intern(uniqueId)) {

            if (cacheManager.getIfPresent(uniqueId) != null) {
                return Result.SUCCESS();
            }

            Date date = new Date();
            Account account = accountRepository.findOne(Example.of(Account.builder().accountNo(flowStat.getAccountNo()).build())).orElse(null);
            if (account == null) {
                log.warn("找不到对应的account");
                return Result.SUCCESS();
            }
            //账号到期
            if (!account.getToDate().after(new Date())) {

                V2RayProxyEvent v2RayProxyEvent = proxyEventService.buildV2RayProxyEvent(account, ProxyEvent.RM_EVENT);
                proxyEventService.addProxyEvent(v2RayProxyEvent);
           /* account.setStatus(0);
            accountService.updateAccount(account);*/
                log.warn("账号到期。,{},{}", account.getAccountNo(), Utils.formatDate(account.getToDate(), null));
                return Result.SUCCESS();
            }
            Integer accountId = account.getId();
            Stat stat = statRepository.findByAccountIdAndFromDateBeforeAndToDateAfter(accountId, date, date);
            if (stat == null) {
                stat = statService.createStat(account);
                //原因：账号到期；增加rm
                if (stat == null) {
                    return Result.SUCCESS();
                }
            }
            Server server = serverRepository.getOne(account.getServerId());
            // 乘 流量倍数
            long used = stat.getFlow() + new Double(flowStat.getUsed() * server.getMultiple()).longValue();
            stat.setFlow(used);
            statRepository.save(stat);
            //防止重复
            cacheManager.put(uniqueId, object);
            //流量超过,增加RM事件
            if ((account.getBandwidth() * G) < used) {
                log.warn("账号流量已经超强限制：{}", account.getAccountNo());
                //不可用状态
                V2RayProxyEvent v2RayProxyEvent = proxyEventService.buildV2RayProxyEvent(account, ProxyEvent.RM_EVENT);
                proxyEventService.addProxyEvent(v2RayProxyEvent);

            }
            return Result.SUCCESS();
        }
    }

    public static void main(String[] args) {
        System.out.println(STRING_WEAK_POLL.intern("a")== STRING_WEAK_POLL.intern(new String("a")));
    }
}
