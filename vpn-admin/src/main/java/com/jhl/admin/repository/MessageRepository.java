package com.jhl.admin.repository;

import com.jhl.admin.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends  JpaRepository<Message,Integer> {
}
