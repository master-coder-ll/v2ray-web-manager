package com.jhl.admin.service.v2ray;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jhl.admin.model.Account;
import com.jhl.admin.model.Server;
import com.jhl.admin.model.User;
import com.jhl.admin.repository.AccountRepository;
import com.jhl.admin.repository.UserRepository;
import com.jhl.admin.service.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.jhl.admin.service.v2ray.ProxyEvent.*;

@Slf4j
@Service
public class ProxyEventService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ServerService serverService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    V2rayAccountService v2rayAccountService;

    private LinkedBlockingQueue<ProxyEvent> queue = new LinkedBlockingQueue<>(100);

    public void addProxyEvent(List<? extends ProxyEvent> proxyEvents) {
        for (ProxyEvent proxyEvent : proxyEvents) {
            try {
                boolean succeeded = queue.offer(proxyEvent, 10, TimeUnit.SECONDS);
                if (!succeeded) log.warn("can not add ProxyEvent: {} to queue", JSON.toJSONString(proxyEvent));
            } catch (InterruptedException e) {
                log.error("addProxyEvent error", e);
                Thread.currentThread().interrupt();

            }
        }

    }

    public void addProxyEvent(ProxyEvent proxyEvent) {
        addProxyEvent(Arrays.asList(proxyEvent));
    }

    @PostConstruct
    public void start() {
        new Thread(() -> {

            while (true) {
                try {
                    //block
                    ProxyEvent event = queue.take();
                    log.info("start send event:{},{}", event.getEvent(), JSON.toJSONString(event));
                    switch (event.getEvent()) {
                        case ADD_EVENT:
                        case UPDATE_EVENT:
                            throw new UnsupportedOperationException("已经不在支持");
                        case RM_EVENT:
                            event.rmEvent();
                            break;
                        default:
                            log.error("unSupport Event ........");
                            return;
                    }
                } catch (InterruptedException e) {
                    log.error("event InterruptedException :", e);
                    Thread.currentThread().interrupt();

                    break;

                } catch (Exception e) {
                    log.error("event 队列 抛出异常:", e);
                }
            }

        }).start();

    }

    /*@PreDestroy
    public void destroy() throws InterruptedException {
        int timer = 0;
        while (true) {
            if (queue.size() > 0 && timer < 10) {
                Thread.sleep(200);
                timer++;
            } else break;
        }

    }*/

    public List<V2RayProxyEvent> buildV2RayProxyEvent(Account account, String opName) {
        Assert.notNull(account,"account is null");
        Integer serverId = account.getServerId();
        if (serverId == null) {
            Account builder = Account.builder().build();
            builder.setId(account.getId());
            account = accountRepository.findOne(Example.of(builder)).orElse(null);
        }

        Assert.notNull(account,"account is null");
        Integer userId = account.getUserId();
        User user = userRepository.findById(userId).orElse(null);
        Assert.notNull(user,"user is null");
        List<Server> servers = serverService.listByLevel(account.getLevel());
        List<V2RayProxyEvent> v2RayProxyEvents = new ArrayList<>(servers.size());
        for (Server server : servers)
            v2RayProxyEvents.add(new V2RayProxyEvent(restTemplate, server, account, user.getEmail(), opName, v2rayAccountService));
        return v2RayProxyEvents;

    }

}
