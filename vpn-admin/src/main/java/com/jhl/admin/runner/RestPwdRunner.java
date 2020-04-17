package com.jhl.admin.runner;

import com.jhl.admin.model.User;
import com.jhl.admin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * 根据配置文件还原管理员密码。
 */
@Slf4j
@Component
public class RestPwdRunner implements CommandLineRunner {
    @Autowired
    UserService userService;
    @Value("${admin.email}")
    String email;
    @Value("${admin.password}")
    String password;

    private static final String COMMAND = "RESTPWD";

    @Override
    public void run(String... args) throws Exception {
        Arrays.stream(args).forEach(s -> {
                    if (s.toUpperCase().equals(COMMAND)) {
                        log.info("执行重置管理员密码操作,配置文件中的账号是:{},密码是:{}", email, password);
                        if (!StringUtils.hasText(email)) {
                            log.info("配置文件中邮箱地址为空。重置命令结束");
                            System.exit(-1);
                        }
                        User user = userService.getOneByAdmin(User.builder().email(email).build());

                        if (user == null || user.getId() == null) {
                            log.info("账号不存在，执行新增管理员账号");
                             user = User.builder().email(email).password(userService.encodePassword(password)).nickName("admin").role("admin").status(1).build();
                        } else {
                            user.setPassword(userService.encodePassword(password));
                        }
                        userService.create(user);
                        log.info("restpwd命令执行成功，系统将退出");
                        System.exit(0);

                    }
                }
        );
    }
}
