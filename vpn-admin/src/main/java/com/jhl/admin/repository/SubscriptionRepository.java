package com.jhl.admin.repository;

import com.jhl.admin.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends  JpaRepository<Subscription,Integer> {
}
