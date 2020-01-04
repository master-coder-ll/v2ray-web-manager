package com.jhl.admin.service;

import com.jhl.admin.cache.EmailVCache;
import com.jhl.admin.util.EmailUtils;
import com.jhl.admin.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    EmailVCache emailVCache;
    @Autowired
    EmailUtils emailUtils;


    public void sendVCode(String email) {
        Object v = emailVCache.getCache(email);
        if (v == null) {
            v = Utils.generateVCode();
            emailVCache.setCache(email, v);
        }
        emailUtils.sendEmail(EmailUtils.MailContent.builder()
                .toEmail(new String[]{email}).subject("验证码")
                .msg(String.format(emailUtils.getVCodeTemplate(), v))
                .build());

    }

    public boolean verifyCode(String email, String code) {
        if (StringUtils.isBlank(code)) return false;
        Object cacheCode = emailVCache.getCache(email);
        if (cacheCode != null && code.equals(cacheCode)) {
            emailVCache.rmCache(email);
            return true;
        } else
            return false;
    }
}
