package com.jhl.common.constant;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import sun.security.action.GetPropertyAction;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.AccessController;
import java.util.concurrent.TimeUnit;

@Component
@PropertySource("internal.properties")
@Getter
@Setter
@Slf4j
public class InternalConstant {
    @Value("${app.version}")
    private String version;
    @Autowired
    RestTemplate restTemplate;
    @Value("${app.githubURL}")
    String githubURL;
   private final  static String V2RAY_RESTART_COMMAND = "systemctl restart v2ray";
    @PostConstruct
    public void init()   {
        new Thread(()->{

            // 修复新版v2ray 安全特性更新导致科学不了的问题
            fixAid();

            JSONObject gitHubJson = restTemplate.getForObject(githubURL, JSONObject.class);
            if (gitHubJson ==null) return;
            String tagName = gitHubJson.getString("tag_name");
            String body = gitHubJson.getString("body").replaceAll("#","");
            log.info("===========================================");
            log.info("Welcome to v2ray-web-manager  : " + version );
            log.info("The latest version :{}",tagName);
            log.info("The latest update :{}",body.trim());
            log.info("===========================================");
        }).start();


    }

    private  void fixAid() {
        String aeadEnv = "Environment=\"V2RAY_VMESS_AEAD_FORCED=false\"".trim();
        File file = new File("/etc/systemd/system/v2ray.service".trim());
        if (file.exists() && file.canRead() && file.canWrite()) {
            boolean existAeadEnv = isExistAeadEnv(aeadEnv, file);
            if (!existAeadEnv) {
                writeEev(aeadEnv, file);
                log.info("写入v2ray兼容旧版环境变量：{}",aeadEnv);
                try {
                    Runtime.getRuntime().exec("systemctl daemon-reload").waitFor(2, TimeUnit.SECONDS);
                    Runtime.getRuntime().exec(V2RAY_RESTART_COMMAND).waitFor(2, TimeUnit.SECONDS);
                    log.info("执行重启v2ray：{}",V2RAY_RESTART_COMMAND);
                } catch (Exception e) {
                    log.error("兼容旧版v2ray重启失败，请手动重启v2ray" ,e);
                }
            }
        }

    }

    private  void writeEev(String AeadEnv, File file) {

        String lineSeparator = AccessController.doPrivileged(
                new GetPropertyAction("line.separator"));
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String tempString;
            // 判断是否已经存在非强制实用新版AEAD加密的环境变量
            while ((tempString = reader.readLine()) != null) {
                sb.append(tempString).append(lineSeparator);
                if ("[Service]".trim().equalsIgnoreCase(tempString)){
                    sb.append(AeadEnv).append(lineSeparator);
                }
            }
        } catch (IOException e) {
            log.error("读取文件失败", e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(sb.toString());
        } catch (IOException e) {
            log.error("写入环境兼容旧版环境变量失败", e);
        }

    }

    private  boolean isExistAeadEnv(String AeadEnv, File file) {
        boolean existAeadEnv = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String tempString;
            // 判断是否已经存在非强制实用新版AEAD加密的环境变量
            while ((tempString = reader.readLine()) != null) {
                if (AeadEnv.equalsIgnoreCase(tempString)) {
                    existAeadEnv = true;
                    break;
                }
            }
        } catch (IOException e) {
            log.error("读取环境兼容旧版环境变量失败", e);
        }
        return existAeadEnv;
    }

}


