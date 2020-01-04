package com.jhl.admin.service.v2ray;

import com.alibaba.fastjson.JSON;
import com.jhl.admin.model.Account;
import com.jhl.admin.model.Server;
import com.jhl.admin.model.User;
import com.jhl.admin.repository.AccountRepository;
import com.jhl.admin.repository.ServerRepository;
import com.jhl.admin.repository.UserRepository;
import com.jhl.admin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingQueue;

import static com.jhl.admin.service.v2ray.ProxyEvent.*;

@Slf4j
@Service
public class ProxyEventService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ServerRepository serverRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    UserService userService;
    private LinkedBlockingQueue<ProxyEvent> queue = new LinkedBlockingQueue<>();


    public void addProxyEvent(ProxyEvent proxyEvent) {
        queue.offer(proxyEvent);
    }

    @PostConstruct
    public void start() {
        new Thread(() -> {

            while (true) {
                try {
                    //block
                    ProxyEvent event = queue.take();
                    log.info("start send event:{},{}",event.getEvent(), JSON.toJSONString(event));
                    switch (event.getEvent()) {
                        case ADD_EVENT:
                            event.createEvent();
                            break;
                        case RM_EVENT:
                            event.rmEvent();
                            break;
                        case UPDATE_EVENT:
                            event.updateEvent();
                            break;
                        default:
                            log.error("unSupport Event ........");
                            return;
                    }
                } catch (InterruptedException e){
                    log.error("event InterruptedException :{}",e);
                    break;

                }catch (Exception e) {
                    log.error("event 队列 抛出异常:{}", e);
                }
            }

        }).start();

    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        int timer = 0;
        while (true) {
            if (queue.size() > 0 && timer < 10) {
                Thread.sleep(200);
                timer++;
            } else break;
        }

    }

    public V2RayProxyEvent buildV2RayProxyEvent(Account account, String opName) {
        Integer serverId = account.getServerId();
        if (serverId == null) {
            Account builder = Account.builder().build();
            builder.setId(account.getId());
            account = accountRepository.findOne(Example.of(builder)).orElse(null);
            serverId = account.getServerId();
        }
        Server server = serverRepository.findById(serverId).orElse(null);
        Integer userId = account.getUserId();
        User user = userRepository.findById(userId).orElse(null);

        return new V2RayProxyEvent(restTemplate, server, account, user.getEmail(), opName);

    }

}
