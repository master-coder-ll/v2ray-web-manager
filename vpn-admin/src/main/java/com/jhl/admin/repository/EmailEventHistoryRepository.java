package com.jhl.admin.repository;

import com.jhl.admin.model.EmailEventHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailEventHistoryRepository extends  JpaRepository<EmailEventHistory,Integer> {
}
