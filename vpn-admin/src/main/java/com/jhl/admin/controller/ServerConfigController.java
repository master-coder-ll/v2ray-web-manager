package com.jhl.admin.controller;

import com.jhl.admin.Interceptor.PreAuth;
import com.jhl.admin.VO.ServerConfigVO;
import com.jhl.admin.model.BaseEntity;
import com.jhl.admin.model.ServerConfig;
import com.jhl.admin.repository.ServerConfigRepository;
import com.ljh.common.model.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ServerConfigController {
    @Autowired
    ServerConfigRepository serverConfigRepository;


    /**
     * 增加
     *
     * @return
     */
    @PreAuth("admin")
    @ResponseBody
    @PostMapping("/serverConfig")
    public Result addServerConfig(@RequestBody ServerConfigVO serverConfig) {
        if (serverConfig == null || StringUtils.isBlank(serverConfig.getKey()) || StringUtils.isBlank(serverConfig.getValue())) {
            throw new NullPointerException("参数不能为空");

        }
            serverConfig.setScope("config");
        serverConfigRepository.save(serverConfig.toModel(ServerConfig.class));
        return Result.SUCCESS();
    }

    /**
     * update
     *
     * @param serverConfig
     * @return
     */
    @PreAuth("admin")
    @ResponseBody
    @PutMapping("/serverConfig")
    public Result update(@RequestBody ServerConfigVO serverConfig) {
        if (serverConfig == null && serverConfig.getId() == null) throw new NullPointerException("id 不能为空");
        return addServerConfig(serverConfig);
    }

    /**
     * list
     *
     * @param page
     * @param pageSize
     * @return
     */
    @PreAuth("admin")
    @ResponseBody
    @GetMapping("/serverConfig")
    public Result list(Integer page, Integer pageSize) {

        Page<ServerConfig> serverConfigs = serverConfigRepository.findAll(Example.of(ServerConfig.builder().scope("config").build()),
                PageRequest.of(page - 1, pageSize));

        List<ServerConfig> content = serverConfigs.getContent();

        return Result.buildPageObject(serverConfigs.getTotalElements(), BaseEntity.toVOList(content, ServerConfigVO.class) );
    }

    @PreAuth("admin")
    @ResponseBody
    @GetMapping("/serverConfig/{id}")
    public Result get(@PathVariable Integer id) {

        if (id == null) new NullPointerException("id 不能为空");
        ServerConfig serverConfig = serverConfigRepository.findById(id).orElse(null);
        return Result.buildSuccess(serverConfig.toVO(ServerConfigVO.class), null);
    }

    @PreAuth("admin")
    @ResponseBody
    @DeleteMapping("/serverConfig/{id}")
    public Result delete(@PathVariable Integer id) {
        if (id == null) new NullPointerException("id 不能为空");
        serverConfigRepository.deleteById(id);
        return Result.SUCCESS();
    }


}
