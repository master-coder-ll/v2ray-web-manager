package com.jhl.admin.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jhl.admin.VO.AccountVO;
import com.jhl.admin.VO.StatVO;
import com.jhl.admin.VO.UserVO;
import com.jhl.admin.constant.KVConstant;
import com.jhl.admin.constant.ProxyConstant;
import com.jhl.admin.entity.V2rayAccount;
import com.jhl.admin.model.*;
import com.jhl.admin.repository.AccountRepository;
import com.jhl.admin.repository.ServerRepository;
import com.jhl.admin.repository.StatRepository;
import com.jhl.admin.service.v2ray.ProxyEvent;
import com.jhl.admin.service.v2ray.ProxyEventService;
import com.jhl.admin.service.v2ray.V2rayAccountService;
import com.jhl.admin.util.Utils;
import com.jhl.admin.util.Validator;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    StatRepository statRepository;
    @Autowired
    ProxyEventService proxyEventService;
    @Autowired
    StatService statService;
    @Autowired
    V2rayAccountService v2rayAccountService;
    @Autowired
    UserService userService;
    @Autowired
    SubscriptionService subscriptionService;


    @Autowired
    private ProxyConstant proxyConstant;
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
            account.setCycle(KVConstant.DAY);
        }
        if (account.getMaxConnection() == null) account.setMaxConnection(32);
        if (account.getToDate() == null)
            account.setToDate(Utils.getDateBy(fromDate, KVConstant.DAY, Calendar.DAY_OF_YEAR));
        account.setStatus(1);
        if (account.getLevel()==null) account.setLevel((short) 0);
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

        accountRepository.save(account);
        Account account1 = accountRepository.findById(account.getId()).orElse(null);
        //判断是否需要生成新的stat
        statService.createStat(accountRepository.getOne(account.getId()));
        //删除事件
            proxyEventService.addProxyEvent(
                    proxyEventService.buildV2RayProxyEvent(account1, ProxyEvent.RM_EVENT));
    }
    @Deprecated
    @Transactional
    public void updateAccountServer(Account account) {
        Integer id = account.getId();
        Account dbAccount = accountRepository.findById(id).orElse(null);
        if (dbAccount.getStatus() == 0 || !dbAccount.getToDate().after(new Date())) {
            throw new IllegalStateException("账号不可用");
        }



        Integer newServerId = account.getServerId();
        Server newServer = serverRepository.findById(newServerId).orElse(null);
        if (newServer == null) throw new NullPointerException("服务器为空");

        /*V2rayAccount v2rayAccount = new V2rayAccount();
        v2rayAccount.setId(uuid);
        v2rayAccount.setAdd(newServer.getClientDomain());
        v2rayAccount.setPort(String.valueOf(newServer.getClientPort()));
        v2rayAccount.setPath(String.format(newServer.getWsPath(), dbAccount.getAccountNo()));
        v2rayAccount.setTls(newServer.getSupportTLS() ? "tls" : "");
        v2rayAccount.setHost("");
        v2rayAccount.setPs(newServer.getDesc());*/
        List<V2rayAccount> v2rayAccounts = v2rayAccountService.buildV2rayAccount(Lists.newArrayList(newServer), dbAccount);
        if (v2rayAccounts.size() != 1) throw new RuntimeException("数据不对");
        account.setContent(JSON.toJSONString(v2rayAccounts.get(0)));
        accountRepository.save(account);


       /*
        String email = userService.get(dbAccount.getUserId()).getEmail();
       //删除旧服务器账号
        if (oldServerId != null) {
            Server oldServer = serverRepository.getOne(oldServerId);
              Account toSendAccount =new Account();
            BeanUtils.copyProperties(dbAccount,toSendAccount);
            V2RayProxyEvent rmEvent = new V2RayProxyEvent(restTemplate, oldServer,toSendAccount , email, ProxyEvent.RM_EVENT);
            proxyEventService.addProxyEvent(rmEvent);
        }


        //同时也确保删除新服务器账号，好重新获取
       Account newAccount = accountRepository.findById(id).orElse(null);
        V2RayProxyEvent rmEvent = new V2RayProxyEvent(restTemplate, newServer,newAccount , email, ProxyEvent.RM_EVENT);
        proxyEventService.addProxyEvent(rmEvent);*/

    }

    /**
     * 获取一个用户下面的账号.并且填充
     *
     * @param userId
     * @return
     */
    public List<AccountVO> getAccounts(Integer userId) {

        Date date = new Date();

        List<Account> accounts = accountRepository.findAll(Example.of(Account.builder().userId(userId).build()));
            List<AccountVO> accountVOList = Lists.newArrayListWithCapacity(accounts.size());
        accounts.forEach(account -> {
            AccountVO accountVO = account.toVO(AccountVO.class);
            fillAccount(date, accountVO);
            accountVOList.add(accountVO);

        });
        return accountVOList;
    }

    public Account getAccount(Integer userId) {


        List<Account> accounts = accountRepository.findAll(Example.of(Account.builder().userId(userId).build()));
        if (accounts.size() >1) throw new IllegalArgumentException("用户存在多个账号，请修复");
        return accounts.isEmpty()?null:accounts.get(0);
    }
    public void fillAccount(Date date, AccountVO account) {
        Integer accountId = account.getId();
        Stat stat = statRepository.findByAccountIdAndFromDateBeforeAndToDateAfter(accountId, date, date);

        Integer userId = account.getUserId();
        User user = userService.getUserButRemovePW(userId);
        if (user != null) account.setUserVO(user.toVO(UserVO.class));
        if (stat != null) account.setStatVO(stat.toVO(StatVO.class));
    }

    /**
     * https://xxx/subscribe/{code}?type=?&timestamp=?&token=?
     * <p>
     * code code
     * type 订阅类型0通用,1....预留
     * token  md5(code+timestamp+api.auth)
     *
     * @param accountId
     */

    public void generatorSubscriptionUrl(Integer accountId, Integer type) {
        Subscription    subscription = subscriptionService.findByAccountId(accountId);
            if (subscription==null){
                subscription = Subscription.builder().accountId(accountId).code(subscriptionService.generatorCode()).build();
            }else {
                subscription.setCode(subscriptionService.generatorCode());

            }
        subscriptionService.addSubscription(subscription);

      Account account = accountRepository.findById(accountId).orElse(null);
        long timeStamp =System.currentTimeMillis();

        String token = DigestUtils.md5Hex(subscription.getCode()+timeStamp+proxyConstant.getAuthPassword());
        String url = String.format(proxyConstant.getSubscriptionTemplate(), subscription.getCode(), 0, timeStamp, token);
        account.setSubscriptionUrl(url);
        accountRepository.save(account);
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
