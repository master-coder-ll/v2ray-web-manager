package com.jhl.admin.service.v2ray;


import com.google.common.collect.Lists;
import com.jhl.admin.entity.V2rayAccount;
import com.jhl.admin.model.Account;
import com.jhl.admin.model.Server;
import com.ljh.common.model.ProxyAccount;
import com.ljh.common.model.Result;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class V2RayProxyEvent implements ProxyEvent {
    private RestTemplate restTemplate;

    private Server server;
    private Account account;
    private String email;
    private String event;
    private V2rayAccountService v2rayAccountService;


    @Override
    public String getEvent() {
        return event;
    }


    @Deprecated
    public void updateEvent() {
        createEvent();
    }

    public void rmEvent() {
        List<String> urls = buildProxyServerUrl();

        ProxyAccount proxyAccount = buildProxyAccount();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(proxyAccount, headers);
        for (String url : urls) {
            try {
                ResponseEntity<Result> responseEntity = restTemplate.postForEntity(url + "/del", entity, Result.class);
                Result result = responseEntity.getBody();
                if (result!=null && result.getCode() != 200) {
                    log.error("远程调用失败,事件：{},result:{}", getEvent(), result);
                }
            } catch (Exception e) {
                log.error("请求发生异常:{}", e.getLocalizedMessage());
            }

        }

    }

    @Deprecated
    public void createEvent() {
        ProxyAccount proxyAccount = buildProxyAccount();
        List<String> urls = buildProxyServerUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(proxyAccount, headers);
        for (String url : urls) {
            try {
                ResponseEntity<Result> responseEntity = restTemplate.postForEntity(url, entity, Result.class);
                Result result = responseEntity.getBody();
                if (result!=null &&result.getCode() != 200) {
                    log.error("远程调用失败,事件：{},result:{}", getEvent(), result);
                }
            } catch (Exception e) {
                log.error("请求发生异常:{}", e.getLocalizedMessage());
            }

        }

    }

    public ProxyAccount buildProxyAccount() {


        ProxyAccount proxyAccount = new ProxyAccount();
        //V2rayAccount v2rayAccount = JSON.parseObject(account.getContent(), V2rayAccount.class);
        String id
                = account.getUuid() == null ?
                v2rayAccountService.buildV2rayAccount(Lists.newArrayList(server), account).get(0).getId()
                : account.getUuid();
        proxyAccount.setAccountId(account.getId());
        proxyAccount.setAccountNo(account.getAccountNo());
        proxyAccount.setAlterId(0);
        proxyAccount.setDownTrafficLimit(account.getSpeed());
        proxyAccount.setEmail(email);
        proxyAccount.setId(id);
        proxyAccount.setInBoundTag(server.getInboundTag());
        proxyAccount.setMaxConnection(account.getMaxConnection());
        proxyAccount.setUpTrafficLimit(account.getSpeed());
        proxyAccount.setV2rayHost(server.getV2rayIp());
        proxyAccount.setV2rayPort(server.getV2rayPort());
        proxyAccount.setV2rayManagerPort(server.getV2rayManagerPort());
        proxyAccount.setHost(server.getClientDomain());
        proxyAccount.setProxyIp(server.getProxyIp());
        return proxyAccount;
    }

    public List<String> buildProxyServerUrl() {
        String[] proxyIps = server.getProxyIp().split(",");
        List<String> urls = Lists.newArrayList();
        for (String proxyIp : proxyIps) {
            urls.add("http://" + proxyIp + ":" + server.getProxyPort() + "/proxyApi/account");
        }

        return urls;
    }


    public static void main(String[] args) {
        V2rayAccount v2rayAccount = new V2rayAccount();

        System.out.println(v2rayAccount.getAid());
    }

}
