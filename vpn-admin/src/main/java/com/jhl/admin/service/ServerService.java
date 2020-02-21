package com.jhl.admin.service;

import com.jhl.admin.constant.enumObject.StatusEnum;
import com.jhl.admin.model.Server;
import com.jhl.admin.repository.ServerRepository;
import com.jhl.admin.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerService {

    @Autowired
    ServerRepository serverRepository;


    public List<Server> listByLevel(Short level) {
        Validator.isNotNull(level);
        List<Server> all = serverRepository.findByLevelLessThanEqualAndStatusOrderByLevelDesc(level, StatusEnum.SUCCESS.code());
        return all;
    }

    /**
     * 增加服务器前，对域名进行检测 唯一
     *
     * @param server
     */
    public void save(Server server) {
        List<Server> all = serverRepository.findAll(Example.of(Server.builder().clientDomain(server.getClientDomain()).build()));
        if (all.size() > 0) {
            throw new IllegalArgumentException("访问域名已经存在/the domain name already exists");
        }
        serverRepository.save(server);

    }

    /**
     * 根据域名查找服务器
     *
     * @param domain
     * @return
     */
    public Server findByDomain(String domain, short level) {
        List<Server> all = serverRepository.findByLevelLessThanEqualAndStatusAndClientDomainOrderByLevelDesc(level, StatusEnum.SUCCESS.code(), domain);
        if (all.size() != 1)
            throw new IllegalArgumentException("1.存在多个相同域名，请删除重复的。2.查找返回为空 ;参数: domain" + domain + ",level:" + level);
        Server server = all.get(0);
        return server;
    }

    public Server findByIdAndStatus(Integer id, Integer status){
        if (status==null) status= StatusEnum.SUCCESS.code();
        Server server = serverRepository.findById(id).orElse(null);
        if (server !=null &&  server.getStatus()!=status){
            server =null;
        }
        return  server;

    }    public void update(Server server) {
        Server checkServer = null;
        try {
            checkServer = findByDomain(server.getClientDomain(), (short) 9);
        } catch (Exception e) {

        }
        if (checkServer == null || checkServer.getId() == server.getId()) {
            serverRepository.save(server);
        } else {
            throw new IllegalArgumentException("已经存在相同的域名");
        }
    }
}
