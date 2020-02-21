package com.jhl.admin.service.init;

import com.jhl.admin.model.Account;
import com.jhl.admin.model.ServerConfig;
import com.jhl.admin.model.User;
import com.jhl.admin.repository.ServerConfigRepository;
import com.jhl.admin.service.AccountService;
import com.jhl.admin.service.StatService;
import com.jhl.admin.service.UserService;
import com.jhl.admin.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
public class AdminInitService {
    @Autowired
    ServerConfigRepository serverConfigRepository;
    @Autowired
    UserService userService;
    @Autowired
    AccountService accountService;
    @Autowired
    StatService statService;
    @PostConstruct
    public void init() {
        ServerConfig initialedConfig = serverConfigRepository.findOne(Example.of(ServerConfig.builder().key("initialed").build())).orElse(null);

        if (initialedConfig != null && Boolean.valueOf(initialedConfig.getValue())) return;
        //todo 新增一个管理员账号
        User user = User.builder().email("371799761@qq.com").password("123456").nickName("jhl").role("admin").status(1).build();
        userService.create(user);
        Account ac = Account.builder().userId(user.getId()).build();
        ac.setCycle(30);
        Date date = Utils.formatDate( new Date(), null);
        ac.setToDate(Utils.getDateBy(date,365, Calendar.DAY_OF_YEAR));
        accountService.create(ac);
        statService.createStat(ac);
        initialedConfig =new ServerConfig();
        initialedConfig.setKey("initialed");
        initialedConfig.setValue("true");
        serverConfigRepository.save(initialedConfig);
    }

    public static void main(String[] args) {
        System.out.println(Boolean.valueOf("true1"));
    }
}
