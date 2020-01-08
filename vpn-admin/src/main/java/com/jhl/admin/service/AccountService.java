package com.jhl.admin.service;

import com.alibaba.fastjson.JSON;
import com.jhl.admin.constant.KVConst;
import com.jhl.admin.entity.V2rayAccount;
import com.jhl.admin.model.Account;
import com.jhl.admin.model.Server;
import com.jhl.admin.model.Stat;
import com.jhl.admin.model.User;
import com.jhl.admin.repository.AccountRepository;
import com.jhl.admin.repository.ServerRepository;
import com.jhl.admin.repository.StatRepository;
import com.jhl.admin.repository.UserRepository;
import com.jhl.admin.service.v2ray.ProxyEvent;
import com.jhl.admin.service.v2ray.ProxyEventService;
import com.jhl.admin.service.v2ray.V2RayProxyEvent;
import com.jhl.admin.util.Utils;
import com.jhl.admin.util.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ServerRepository serverRepository;
    @Autowired
    UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    StatRepository statRepository;
    @Autowired
    ProxyEventService proxyEventService;
    @Autowired
    StatService statService;


    /**
     * 新增一个账号
     *
     * @param account
     * @return
     */
    public Account create(Account account) {
        Validator.isNotNull(account.getUserId());
        Date date = new Date();
        if (account.getBandwidth() == null) {
            account.setBandwidth(2);
        }


        account.setAccountNo(Utils.getCharAndNum(7));
        //1024kb/S
        if (account.getSpeed() == null) account.setSpeed(1024L);

        Date fromDate = Utils.formatDate(date, null);
        if (account.getFromDate() == null) account.setFromDate(fromDate);
        if (account.getCycle() == null) {
                account.setCycle(KVConst.DAY);
        }
        if (account.getMaxConnection() == null) account.setMaxConnection(32);
       if (account.getToDate()==null) account.setToDate(Utils.getDateBy(fromDate, KVConst.DAY, Calendar.DAY_OF_YEAR));
        account.setStatus(1);
        accountRepository.save(account);

        return account;
    }

    /**
     * 更新账号的信息，不涉及服务器相关/content相关
     */
    public void updateAccount(Account account) {
        Validator.isNotNull(account.getId());
        account.setContent(null);
        account.setServerId(null);
        Account old = accountRepository.findById(account.getId()).orElse(null);
        Integer serverId = old.getServerId();
        accountRepository.save(account);
        //判断是否需要生成新的stat
        statService.createStat(accountRepository.getOne(account.getId()));
        //删除事件
        if (serverId !=null) {
            proxyEventService.addProxyEvent(
                    proxyEventService.buildV2RayProxyEvent(old, ProxyEvent.RM_EVENT));
        }
    }

    @Transactional
    public void updateAccountServer(Account account) {
        Integer id = account.getId();
        Account dbAccount = accountRepository.findById(id).orElse(null);
        if (dbAccount.getStatus() ==0 || !dbAccount.getToDate().after(new Date())) {
            throw  new IllegalStateException("账号不可用");
        }

        V2rayAccount dbV2ray = JSON.parseObject(dbAccount.getContent(), V2rayAccount.class);
        //新建的
        Integer oldServerId = dbAccount.getServerId();
        //处理v2rayAccount
        String uuid = StringUtils.isBlank(dbAccount.getContent()) ?
                UUID.randomUUID().toString()
                : dbV2ray.getId();

        Integer newServerId = account.getServerId();
        //如果为空
       // if (oldServerId == newServerId) return;
        Server newServer = serverRepository.findById(newServerId).orElse(null);
        if (newServer == null) throw new NullPointerException("服务器为空");

        V2rayAccount v2rayAccount = new V2rayAccount();
        v2rayAccount.setId(uuid);
        v2rayAccount.setAdd(newServer.getClientDomain());
        v2rayAccount.setPort(String.valueOf(newServer.getClientPort()));
        v2rayAccount.setPath(String.format(newServer.getWsPath(), dbAccount.getAccountNo()));
        v2rayAccount.setTls(newServer.getSupportTLS() ? "tls" : "");
        v2rayAccount.setHost("");
        account.setContent(JSON.toJSONString(v2rayAccount));



        String email = userService.get(dbAccount.getUserId()).getEmail();
        //删除旧服务器账号
        if (oldServerId != null) {
            Server oldServer = serverRepository.getOne(oldServerId);
              Account toSendAccount =new Account();
            BeanUtils.copyProperties(dbAccount,toSendAccount);
            V2RayProxyEvent rmEvent = new V2RayProxyEvent(restTemplate, oldServer,toSendAccount , email, ProxyEvent.RM_EVENT);
            proxyEventService.addProxyEvent(rmEvent);
        }

        accountRepository.save(account);
        //同时也确保删除新服务器账号，好重新获取
       Account newAccount = accountRepository.findById(id).orElse(null);
        V2RayProxyEvent rmEvent = new V2RayProxyEvent(restTemplate, newServer,newAccount , email, ProxyEvent.RM_EVENT);
        proxyEventService.addProxyEvent(rmEvent);


    /*
    //不要需要主动添加 通过懒加载的方式，proxy端获取
    V2RayProxyEvent rmEvent = new V2RayProxyEvent(restTemplate, newServer,dbAccount , email, ProxyEvent.RM_EVENT);
        proxyEventService.addProxyEvent(rmEvent);
        V2RayProxyEvent addEvent = new V2RayProxyEvent(restTemplate, newServer, dbAccount, email, ProxyEvent.ADD_EVENT);
        proxyEventService.addProxyEvent(addEvent);*/
    }

    /**
     * 获取一个用户下面的账号+最近stat
     *
     * @param userId
     * @return
     */
    public List<Account> getAccounts(Integer userId) {

        Date date = new Date();

        List<Account> accounts = accountRepository.findAll(Example.of(Account.builder().userId(userId).build()));

        accounts.forEach(account -> {
            fillAccount(date, account);

        });
        return accounts;
    }

    public void fillAccount(Date date, Account account) {
        Integer accountId = account.getId();
        Stat stat = statRepository.findByAccountIdAndFromDateBeforeAndToDateAfter(accountId, date, date);

        Integer userId = account.getUserId();
        User user = userRepository.findById(userId).orElse(null);
        if (user !=null) account.setEmail(user.getEmail());
        if (stat != null) account.setStat(stat);
        Integer serverId = account.getServerId();
        if (serverId != null) {
            Server server = serverRepository.findById(serverId).orElse(null);
            if (server != null) account.setServer(server);
        }
    }

/*
    public List<Account> listAllAccount(List<User> users) {
        ArrayList<Account> allAccounts = Lists.newArrayList();
        users.forEach(user -> {
            List<Account> accounts = getAccounts(user.getId());
            allAccounts.addAll(allAccounts);
        });
        return allAccounts;
    }
*/


    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString());
    }
}
