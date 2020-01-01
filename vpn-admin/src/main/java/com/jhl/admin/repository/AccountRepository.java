package com.jhl.admin.repository;

import com.jhl.admin.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AccountRepository extends  JpaRepository<Account,Integer> {

    List<Account> findByToDateAfter(Date date);
    @Query(value = "select a.* from account a join user u on a.user_id =u.id where u.email like ?1",nativeQuery = true)
    List<Account> findByUserEmail(String email);
}
