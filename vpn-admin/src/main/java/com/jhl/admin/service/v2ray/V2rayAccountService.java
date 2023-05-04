package com.jhl.admin.service.v2ray;

import com.alibaba.fastjson.JSON;
import com.jhl.admin.constant.ProxyConstant;
import com.jhl.admin.entity.V2rayAccount;
import com.jhl.admin.model.Account;
import com.jhl.admin.model.Server;
import com.jhl.admin.model.Stat;
import com.jhl.admin.repository.AccountRepository;
import com.jhl.admin.service.StatService;
import com.jhl.admin.util.Utils;
import com.ljh.common.utils.V2RayPathEncoder;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class V2rayAccountService {
    @Autowired
    ProxyConstant proxyConstant;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    StatService statService;

    public String buildB64V2rayAccount(List<Server> servers, Account account) {
        StringBuilder sb = new StringBuilder();
        Base64.Encoder encoder = Base64.getEncoder();
        // tip
        if (new Date().getTime()>account.getToDate().getTime()){
            V2rayAccount alertMessage = new V2rayAccount();
            alertMessage.setPs("通知-账号已经过期，请联系管理员:"+Utils.toDateStr(account.getToDate(),null));
            sb.append("vmess://").append(encoder.encodeToString(JSON.toJSONString(alertMessage).getBytes(StandardCharsets.UTF_8))).append("\n");
            return  sb.toString();
        }


        for (V2rayAccount v2rayAccount : buildV2rayAccount(servers, account)) {
            String encode = encoder.encodeToString(JSON.toJSONString(v2rayAccount).getBytes(StandardCharsets.UTF_8));
            sb.append("vmess://").append(encode).append("\n");
        }

        // tip 流量 和 有效期
        V2rayAccount tip = new V2rayAccount();
        Stat stat = statService.createOrGetStat(account);
        tip.setPs("通知-流量:"+stat.getFlow()/1024/1024/1024+"/"+account.getBandwidth()+"G;流量重置时间:"+Utils.toDateStr(stat.getToDate(),"yyyy-MM-dd HH时"));
        V2rayAccount tip2 = new V2rayAccount();
        tip2.setPs("通知-账号有效期至:"+ Utils.toDateStr(account.getToDate(),null));

        sb.append("vmess://").append(encoder.encodeToString(JSON.toJSONString(tip).getBytes(StandardCharsets.UTF_8))).append("\n");
        sb.append("vmess://").append(encoder.encodeToString(JSON.toJSONString(tip2).getBytes(StandardCharsets.UTF_8))).append("\n");

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
