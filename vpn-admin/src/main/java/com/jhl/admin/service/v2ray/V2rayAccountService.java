package com.jhl.admin.service.v2ray;

import com.alibaba.fastjson.JSON;
import com.jhl.admin.constant.ProxyConstant;
import com.jhl.admin.entity.V2rayAccount;
import com.jhl.admin.model.Account;
import com.jhl.admin.model.Server;
import com.jhl.admin.repository.AccountRepository;
import com.ljh.common.utils.V2RayPathEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class V2rayAccountService {
    @Autowired
    ProxyConstant proxyConstant;
    @Autowired
    AccountRepository accountRepository;

    public String buildB64V2rayAccount(List<Server> servers, Account account) {
        StringBuilder sb = new StringBuilder();
        for (V2rayAccount v2rayAccount : buildV2rayAccount(servers, account)) {
            String encode = Base64.getEncoder().encodeToString(JSON.toJSONString(v2rayAccount).getBytes(StandardCharsets.UTF_8));
            sb.append("vmess://").append(encode).append("\n");
        }

        return sb.toString();
    }

    public List<V2rayAccount> buildV2rayAccount(List<Server> servers, Account account) {

        String uuid = account.getUuid();
        if (account.getUuid() == null) {
            //兼容旧版
            V2rayAccount oldV2ray = JSON.parseObject(account.getContent(), V2rayAccount.class);
            uuid = oldV2ray == null ? UUID.randomUUID().toString() : oldV2ray.getId();
            account.setUuid(uuid);
            accountRepository.save(account);
        }
        List<V2rayAccount> result = new ArrayList<>(servers.size());
        for (Server s : servers) {
            V2rayAccount v2rayAccount = new V2rayAccount();
            v2rayAccount.setId(uuid);
            v2rayAccount.setAdd(s.getClientDomain());
            v2rayAccount.setPort(String.valueOf(s.getClientPort()));
            String token = V2RayPathEncoder.encoder(account.getAccountNo(), s.getClientDomain(), proxyConstant.getAuthPassword());
            v2rayAccount.setPath(String.format(s.getWsPath(), account.getAccountNo()+":"+token));
            v2rayAccount.setTls(s.getSupportTLS() ? "tls" : "");
            v2rayAccount.setHost("");
            v2rayAccount.setPs(s.getServerName());
            v2rayAccount.setAid(s.getAlterId()+"");
            result.add(v2rayAccount);
        }
        return result;
    }
}
