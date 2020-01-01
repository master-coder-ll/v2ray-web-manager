package com.jhl.admin.cron;

import com.jhl.admin.model.Account;
import com.jhl.admin.model.Stat;
import com.jhl.admin.repository.AccountRepository;
import com.jhl.admin.repository.ServerRepository;
import com.jhl.admin.repository.StatRepository;
import com.jhl.admin.service.StatService;
import com.jhl.admin.service.v2ray.ProxyEvent;
import com.jhl.admin.service.v2ray.ProxyEventService;
import com.jhl.admin.service.v2ray.V2RayProxyEvent;
import com.jhl.admin.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class AppCron {

    @Autowired
    AccountRepository accountRepository;


    @Autowired
    StatService statService;
    @Autowired
    ProxyEventService proxyEventService;

    @Async
    @Scheduled(cron = "0 0 */1 * * ?")
    public void createStatTimer() {
        Date today = new Date();
        log.info("构建下个stat任务。。开始，{}",today);
        List<Account> accounts = accountRepository.findByToDateAfter(today);
        if (accounts == null) return;
        accounts.forEach(account -> {

            statService.createStat(account);
        });


    }



    // @Scheduled(cron = "0 */1 * * * ?")
   // public void test() {
   //     log.info("1分钟执行一次。。。");
   // }
    /**
     * 检查过期的账号 并且发送删除命令
     */
    @Deprecated
    @Async
   // @Scheduled(cron = "0 0 */1 * * ?")
    public void checkInvalidAccount() {
        log.info("检查账号任务。。开始，{}",new Date());
        List<Account> accountList = accountRepository.findAll(Example.of(Account.builder().status(1).build()));
        accountList.forEach(account -> {
            Date now = new Date();
            Date toDate = account.getToDate();
            if (toDate.after(now)) return;
          //  account.setStatus(0);
          //  accountRepository.save(account);
            try {
                V2RayProxyEvent v2RayProxyEvent = proxyEventService.buildV2RayProxyEvent(account, ProxyEvent.RM_EVENT);
                 proxyEventService.addProxyEvent(v2RayProxyEvent);
            } catch (Exception e) {
                //
            }
        });

    }
}
