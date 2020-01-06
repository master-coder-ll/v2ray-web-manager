package com.jhl.admin.repository;

import com.jhl.admin.model.InvitationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationCodeRepository extends  JpaRepository<InvitationCode,Integer> {
}
