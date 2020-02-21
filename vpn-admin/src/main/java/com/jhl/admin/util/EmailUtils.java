package com.jhl.admin.util;

import com.jhl.admin.constant.EmailConstant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailUtils {
    @Autowired
    EmailConstant emailConstant;
    @Async
    public void sendEmail(MailContent mailContent) {
        try {

            HtmlEmail email = new HtmlEmail();

        //    email.setSslSmtpPort(port+"");
            email.setSmtpPort(emailConstant.getPort());
            email.setCharset("UTF-8");
            email.setHostName(emailConstant.getHost());
            email.setFrom(emailConstant.getUserName());
            email.setAuthentication(emailConstant.getUserName(), emailConstant.getPassword());
            email.addTo(mailContent.getToEmail());
            email.setSubject(mailContent.getSubject());
            email.setTextMsg(mailContent.getMsg());
            // 不校验ssl
            email.setSSLCheckServerIdentity(false);
            email.setStartTLSEnabled(true);
            email.send();

        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    @Getter
    @Setter
    @Builder
    public static class MailContent {
        private String[] toEmail;
        private String msg;
        private String subject;

    }

}
