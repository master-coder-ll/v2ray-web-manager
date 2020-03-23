package com.jhl.admin.controller.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.jhl.admin.cache.ConnectionStatCache;
import com.jhl.admin.constant.EmailConstant;
import com.jhl.admin.constant.enumObject.EmailEventEnum;
import com.jhl.admin.model.*;
import com.jhl.admin.repository.AccountRepository;
import com.jhl.admin.repository.StatRepository;
import com.jhl.admin.service.*;
import com.jhl.admin.service.v2ray.ProxyEvent;
import com.jhl.admin.service.v2ray.ProxyEventService;
import com.jhl.admin.service.v2ray.V2RayProxyEvent;
import com.jhl.admin.util.Utils;
import com.ljh.common.model.FlowStat;
import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    ServerService serverService;
    private long G = 1024 * 1024 * 1024;
    @Autowired
    EmailService emailService;
    @Autowired
    UserService userService;
    @Autowired
    ProxyEventService proxyEventService;
    @Autowired
    EmailConstant emailConstant;
    @Autowired
    ConnectionStatCache connectionStatCache;
    //幂等
    Cache<String, Object> cacheManager = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(1, TimeUnit.MINUTES).build();
    @Autowired
    AccountService accountService;
    private static byte object = 1;


    @ResponseBody
    @PostMapping("/flowStat")
    public Result flowStat(@RequestBody FlowStat flowStat) {


        if (flowStat == null) {
            log.error("收到空的  flowStat report。。");
            return Result.SUCCESS();
        }
        String uniqueId = flowStat.getUniqueId();

        if (cacheManager.getIfPresent(uniqueId) != null) {
            log.warn("重复的stat上报{}", uniqueId);
            return Result.SUCCESS();
        }
        synchronized (Utils.getInternersPoll().intern(uniqueId)) {

            if (cacheManager.getIfPresent(uniqueId) != null) {
                return Result.SUCCESS();
            }

            Date date = new Date();
            Account account = accountService.findByAccountNo(flowStat.getAccountNo());
            if (account == null) {
                log.warn("找不到对应的account");
                return Result.SUCCESS();
            }
            //账号到期
            if (!account.getToDate().after(new Date())) {

                proxyEventService.addProxyEvent(proxyEventService.buildV2RayProxyEvent(account, ProxyEvent.RM_EVENT));
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
            Server server = serverService.findByDomain(flowStat.getDomain(), account.getLevel());
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
                List<V2RayProxyEvent> v2RayProxyEvents = proxyEventService.buildV2RayProxyEvent(account, ProxyEvent.RM_EVENT);
                proxyEventService.addProxyEvent(v2RayProxyEvents);

            }
            return Result.SUCCESS();
        }
    }

    @ResponseBody
    @GetMapping("/connectionLimit")
    public Result exceedsMaxConnection(String accountNo) {

        if (StringUtils.isBlank(accountNo)) return Result.SUCCESS();

        Account account = accountService.findByAccountNo(accountNo);
        if (account == null) return Result.SUCCESS();

        Integer userId = account.getUserId();
        User user = userService.get(userId);
        String email = user.getEmail();

        emailService.sendEmail(email, "风险系统:检测到你的账号连接数过大",
                emailConstant.getExceedConnections(),
                EmailEventHistory.builder().email(email).
                        event(EmailEventEnum.EXCEEDS_MAX_CONNECTION_EVENT.name())
                        .unlockDate(Utils.getDateBy(new Date(), 1, Calendar.HOUR_OF_DAY))
                        .build());
        return Result.SUCCESS();
    }

    @ResponseBody
    @GetMapping("/connectionStat")
    public Result connectionStat(String accountNo, String host, Integer count) {
        connectionStatCache.add(accountNo, host, count);

        Account account = accountService.findByAccountNo(accountNo);
        Integer maxConnections = account.getMaxConnection();
        int total = connectionStatCache.getTotal(accountNo, maxConnections);
        long lastBlackTime = connectionStatCache.getLastBlackTime(accountNo);
        HashMap<String, Object> result = Maps.newHashMapWithExpectedSize(2);
        result.put("total", total);
        result.put("lastBlackTime", lastBlackTime);
        return Result.buildSuccess(result, null);
    }

    public static void main(String[] args) {
        //System.out.println(STRING_WEAK_POLL.intern("a")== STRING_WEAK_POLL.intern(new String("a")));
    }
}
