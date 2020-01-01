package com.jhl.admin.repository;

import com.jhl.admin.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends  JpaRepository<Server,Integer> {
}
