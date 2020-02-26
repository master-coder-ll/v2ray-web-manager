package com.jhl.admin.controller;

import com.jhl.admin.model.Account;
import com.jhl.admin.model.Server;
import com.jhl.admin.model.Stat;
import com.jhl.admin.model.User;
import com.jhl.admin.repository.AccountRepository;
import com.jhl.admin.repository.ServerRepository;
import com.jhl.admin.repository.StatRepository;
import com.jhl.admin.repository.UserRepository;
import com.jhl.admin.service.ServerService;
import com.jhl.admin.service.StatService;
import com.jhl.admin.service.v2ray.ProxyEvent;
import com.jhl.admin.service.v2ray.ProxyEventService;
import com.jhl.admin.service.v2ray.V2RayProxyEvent;
import com.jhl.admin.service.v2ray.V2rayAccountService;
import com.jhl.admin.util.Utils;
import com.ljh.common.model.ProxyAccount;
import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Slf4j
@Controller
@RequestMapping("/proxy")
public class ProxyController {

    @Autowired
    StatRepository statRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ServerService serverService;

    @Autowired
    ServerRepository serverRepository;
    @Autowired
    UserRepository userRepository;
    private long G = 1024 * 1024 * 1024;
    @Autowired
    V2rayAccountService v2rayAccountService;
    /**
     * 获取一个proxyAccount
     *
     * @param accountNo
     * @param domain  域名/ip
     * @return
     */
    @ResponseBody
    @GetMapping("/proxyAccount/ac")
    public Result getPAccount(String accountNo,String domain) {
       // log.info("account no is:,{}", accountNo);
        if ( StringUtils.isBlank(accountNo)||StringUtils.isBlank(domain) ) {
            return Result.builder().code(500).message("accountNo/domain is null").build();
        }

        Account account = accountRepository.findOne(Example.of(Account.builder().accountNo(accountNo).status(1).build())).orElse(null);
        if (account == null) return Result.builder().code(500).message("账号不存在").build();
        //check date
        if (!account.getToDate().after(new Date())) {
            log.warn("账号到期,不能获取:{},{}", accountNo, Utils.formatDate(account.getToDate(), null));
            return Result.builder().code(500).message("账号到期,不能获取").build();
        }
        Integer accountId = account.getId();
         Date date = new Date();
         Stat stat = statRepository.findByAccountIdAndFromDateBeforeAndToDateAfter(accountId, date, date);
         if (stat ==null) {
             return Result.builder().code(500).message("获取不到stat,原因:未生成stat，已经过期").build();
         }
         //check flow
        if ((account.getBandwidth() * G) < stat.getFlow()) {
            log.warn("账号流量已经超强限制：{}" ,account.getAccountNo());
            return Result.builder().code(500).message("流量已经超过限制").build();
        }

        Integer userId = account.getUserId();

        //新版应该根据域名查找服务器

        Server server=  serverService.findByDomain(domain,account.getLevel());
        //Integer serverId = account.getServerId();

        User user = userRepository.findById(userId).orElse(null);

        V2RayProxyEvent v2RayProxyEvent = new V2RayProxyEvent(null, server, account, user.getEmail(), null,v2rayAccountService);
        ProxyAccount proxyAccount = v2RayProxyEvent.buildProxyAccount();

        return Result.builder().code(Result.CODE_SUCCESS).obj(proxyAccount).build();
    }


}
