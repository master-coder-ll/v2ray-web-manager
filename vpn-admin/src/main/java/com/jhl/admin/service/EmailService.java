package com.jhl.admin.service;

import com.jhl.admin.cache.EmailVCache;
import com.jhl.admin.constant.EmailConstant;
import com.jhl.admin.model.EmailEventHistory;
import com.jhl.admin.repository.EmailEventHistoryRepository;
import com.jhl.admin.util.EmailUtils;
import com.jhl.admin.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class EmailService {
    @Autowired
    EmailVCache emailVCache;
    @Autowired
    EmailUtils emailUtils;
    @Autowired
    EmailConstant emailConstant;
    @Autowired
    EmailEventHistoryRepository emailEventHistoryRepository;

    public void sendVCode(String email) {
        Object v = emailVCache.getCache(email);
        if (v == null) {
            v = Utils.generateVCode();
            emailVCache.setCache(email, v);
        }
        sendEmail(email, "验证码", String.format(emailConstant.getVCodeTemplate(), v), null);

    }

    /**
     * 发送邮件，有对email事件重发过滤
     * @param email 邮件
     * @param subject 主题
     * @param msg 主题
     * @param emailEventHistory 发送记录
     */
    public void sendEmail(String email, String subject, String msg, EmailEventHistory emailEventHistory) {

        String event = emailEventHistory == null ? "" : emailEventHistory.getEvent();

        synchronized (Utils.getInternersPoll().intern(email + event)) {

            if (emailEventHistory != null) {
                EmailEventHistory latestHistory = findLatestHistory(email, event);
                if (latestHistory != null && latestHistory.getUnlockDate().after(new Date())) {
                    log.warn("已经存在email事件的发送记录，跳过当前");
                    return;
                }
            }
            emailUtils.sendEmail(EmailUtils.MailContent.builder()
                    .toEmail(new String[]{email}).subject(subject)
                    .msg(msg)
                    .build());

            if (emailEventHistory != null) {
                emailEventHistoryRepository.save(emailEventHistory);
            }
        }

    }


    public EmailEventHistory findLatestHistory(String email, String event) {
        //寻找email最新event的一条
        Page<EmailEventHistory> eeh = emailEventHistoryRepository.findAll(Example.of(EmailEventHistory.builder().email(email).event(event).build())
                , PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id"))));
        if (eeh.getTotalElements() != 1) return null;
        return eeh.getContent().get(0);

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
