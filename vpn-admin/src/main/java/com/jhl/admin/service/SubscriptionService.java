package com.jhl.admin.service;

import com.jhl.admin.model.Account;
import com.jhl.admin.model.Server;
import com.jhl.admin.model.Subscription;
import com.jhl.admin.repository.AccountRepository;
import com.jhl.admin.repository.SubscriptionRepository;
import com.jhl.admin.service.v2ray.V2rayAccountService;
import com.jhl.admin.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    SubscriptionRepository subscriptionRepository;
    @Autowired
    ServerService serverService;
    @Autowired
    V2rayAccountService v2rayAccountService;

    /**
     * 同一个code 订阅
     *
     * @param code
     * @return base64后的数据
     */
    public String subscribe(String code) {
        Account account = findAccountByCode(code);
        Short level = account.getLevel();

        List<Server> servers = serverService.listByLevel(level);

        String b64V2rayAccount = v2rayAccountService.buildB64V2rayAccount(servers, account);
        //需要再进行一次base64
        return Base64.getEncoder().encodeToString(b64V2rayAccount.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 通过一个订阅code寻找账号
     *
     * @param code
     * @return
     */
    public Account findAccountByCode(String code) {
        if (code == null) throw new IllegalArgumentException("code can't be null");
        Optional<Subscription> subscriptionOptional =
                subscriptionRepository.findOne(Example.of(Subscription.builder().code(code).build()));
        if (!subscriptionOptional.isPresent()) throw new IllegalArgumentException("the code doesn't exist");

        Integer accountId = subscriptionOptional.get().getAccountId();


        return accountRepository.findById(accountId).orElse(null);
    }

    public Subscription findByAccountId(Integer accountId) {
        if (accountId == null) throw new IllegalArgumentException(" can't be null");
        Optional<Subscription> subscriptionOptional =
                subscriptionRepository.findOne(Example.of(Subscription.builder().accountId(accountId).build()));

        return subscriptionOptional.orElse(null);
    }

    public void addSubscription(Subscription subscription) {
        if (subscription == null || subscription.getAccountId() == null) throw new RuntimeException(" can't be null");

        if (subscription.getCode() == null) subscription.setCode(generatorCode());

        subscriptionRepository.save(subscription);

    }

    /**
     * @param code 可以为空
     * @param id
     */
    public void updateSubscription(String code, Integer id) {
        if (code == null) code = generatorCode();
        Subscription subscription = Subscription.builder().code(code).build();
        subscription.setId(id);
        subscriptionRepository.save(subscription);

    }


    public String generatorCode() {

        return Utils.getCharAndNum(10);
    }
}
