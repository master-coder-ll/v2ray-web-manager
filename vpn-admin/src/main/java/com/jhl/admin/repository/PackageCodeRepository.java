package com.jhl.admin.repository;

import com.jhl.admin.model.PackageCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageCodeRepository extends  JpaRepository<PackageCode,Integer> {
}
