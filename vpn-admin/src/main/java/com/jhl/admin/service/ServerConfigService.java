package com.jhl.admin.service;

import com.jhl.admin.constant.WebsiteConfigKV;
import com.jhl.admin.model.ServerConfig;
import com.jhl.admin.repository.ServerConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ServerConfigService {
    @Autowired
    ServerConfigRepository serverConfigRepository;

    //检测配置，并初始化
    @PostConstruct
    public void init() {
        WebsiteConfigKV[] values = WebsiteConfigKV.values();

        for (WebsiteConfigKV kv : values) {
            if (!containKey(kv.getKey())) {
                ServerConfig et = ServerConfig.builder().key(kv.getKey()).value(kv.getValue()).name(kv.getName())
                        .scope(kv.getScope())
                        .build();
                serverConfigRepository.save(et);
            }

        }
    }

    //if true  true ,else  false
    public boolean checkKey(String key) {
        ServerConfig serverConfig = serverConfigRepository.findOne(Example.of(ServerConfig.builder().key(key).build())).orElse(null);
        if (serverConfig != null && serverConfig.getValue().equals("true")) return true;
        else return false;

    }

    public boolean containKey(String key) {
        ServerConfig serverConfig = serverConfigRepository.findOne(Example.of(ServerConfig.builder().key(key).build())).orElse(null);
        if (serverConfig == null) return false;
        else return true;

    }
}
