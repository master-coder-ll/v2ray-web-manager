package com.jhl.admin.repository;

import com.jhl.admin.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends  JpaRepository<Package,Integer> {
}
