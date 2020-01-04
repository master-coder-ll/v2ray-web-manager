package com.jhl.admin.repository;

import com.jhl.admin.model.Account;
import com.jhl.admin.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends  JpaRepository<Order,Integer> {
}
