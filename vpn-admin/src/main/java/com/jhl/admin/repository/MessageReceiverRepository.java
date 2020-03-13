package com.jhl.admin.repository;

import com.jhl.admin.model.Message;
import com.jhl.admin.model.MessageReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageReceiverRepository extends  JpaRepository<MessageReceiver,Integer> {
}
