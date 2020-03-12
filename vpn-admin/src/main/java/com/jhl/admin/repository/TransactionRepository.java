package com.jhl.admin.repository;

import com.jhl.admin.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends  JpaRepository<Transaction,Integer> {
}
