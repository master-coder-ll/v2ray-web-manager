package com.jhl.admin.repository;

import com.jhl.admin.model.Account;
import com.jhl.admin.model.ServerConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerConfigRepository extends  JpaRepository<ServerConfig,Integer> {
}
