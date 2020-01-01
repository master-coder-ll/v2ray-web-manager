package com.jhl.admin.util;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "email")
@PropertySource(value = {"classpath:application.properties"}, encoding = "utf-8")
public class EmailUtils {
    private String userName;
    private String password;
    private String host;
    //验证码消息模板
    private String vCodeTemplate;
    private  int port;
    @Async
    public void sendEmail(MailContent mailContent) {
        try {
            System.setProperty("mail.smtp.ssl.enable", "true");
            HtmlEmail email = new HtmlEmail();
            email.setSmtpPort(port);
            email.setCharset("UTF-8");
            email.setHostName(host);
            email.setFrom(userName);
            email.setAuthentication(userName, password);
            email.addTo(mailContent.getToEmail());
            email.setSubject(mailContent.getSubject());
            email.setTextMsg(mailContent.getMsg());
            email.setSSLOnConnect(true);
           // email.setStartTLSEnabled(true);
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
