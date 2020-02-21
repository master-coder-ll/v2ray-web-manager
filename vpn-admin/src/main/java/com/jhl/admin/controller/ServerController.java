package com.jhl.admin.controller;

import com.jhl.admin.Interceptor.PreAuth;
import com.jhl.admin.cache.UserCache;
import com.jhl.admin.constant.KVConstant;
import com.jhl.admin.model.Account;
import com.jhl.admin.model.Server;
import com.jhl.admin.model.User;
import com.jhl.admin.repository.ServerRepository;
import com.jhl.admin.service.AccountService;
import com.jhl.admin.service.ServerService;
import com.jhl.admin.util.Validator;
import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
public class ServerController {

    @Autowired
    ServerRepository serverRepository;
    @Autowired
    ServerService serverService;
    @Autowired
    UserCache userCache;
    @Autowired
    AccountService accountService;

    @PreAuth("vip")
    @ResponseBody
    @GetMapping("/server/{id}")
    public Result get(@PathVariable Integer id) {
        Validator.isNotNull(id);
        Server server = serverRepository.findById(id).get();
        return Result.builder().code(Result.CODE_SUCCESS).obj(server).build();
    }

    @PreAuth("admin")
    @ResponseBody
    @GetMapping("/server")
    public Result findByPage(Integer page, Integer pageSize, @CookieValue(KVConstant.COOKIE_NAME) String auth) {
        Validator.isNotNull(page);
        Validator.isNotNull(pageSize);
        Page<Server> all = serverRepository.findAll(Example.of(Server.builder().build()), PageRequest.of(page - 1, pageSize));
        ;

        return Result.buildPageObject(all.getTotalElements(), all.getContent());
    }

    @PreAuth("vip")
    @ResponseBody
    @GetMapping("/server/findServersForAccount")
    public Result findServersForAccount(@CookieValue(KVConstant.COOKIE_NAME) String auth) {

        User user = userCache.getCache(auth);
        List<Account> accounts = accountService.getAccounts(user.getId());
        if (accounts.size() != 1) return Result.builder().code(500).message("用户存在多个账号/或者账号为空").build();
        Account account = accounts.get(0);
        Short level = account.getLevel();
        List<Server> servers = serverService.listByLevel(level);
        return Result.buildSuccess(servers, null);
    }

    @PreAuth("admin")
    @ResponseBody
    @DeleteMapping("/server/{id}")
    public Result del(@PathVariable Integer id) {
        Validator.isNotNull(id);
        serverRepository.deleteById(id);
        return Result.builder().code(Result.CODE_SUCCESS).build();
    }

    /**
     * 新增
     *
     * @return
     */
    @PreAuth("admin")
    @ResponseBody
    @PostMapping("/server")
    public Result insert(@RequestBody Server server) {
        Validator.isNotNull(server);
        serverService.save(server);
        return Result.SUCCESS();
    }

    /**
     * 修改
     */
    @PreAuth("admin")
    @ResponseBody
    @PutMapping("/server")
    public Result update(@RequestBody Server server) {
        Validator.isNotNull(server);


       serverService.update(server);

        //todo 修改服务器后的逻辑 1.更新账号2.推送到中间件
        return Result.builder().code(Result.CODE_SUCCESS).build();
    }
}
