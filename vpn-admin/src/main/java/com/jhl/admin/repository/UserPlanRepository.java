package com.jhl.admin.repository;

import com.jhl.admin.model.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlanRepository extends  JpaRepository<UserPackage,Integer> {
}
