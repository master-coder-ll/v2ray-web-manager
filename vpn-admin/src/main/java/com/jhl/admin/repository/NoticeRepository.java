package com.jhl.admin.repository;

import com.jhl.admin.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NoticeRepository extends  JpaRepository<Notice,Integer> {

    /**
     * top7
     * @param status
     * @param date
     * @return
     */
    public List<Notice> findTop7ByStatusAndToDateAfterOrderByUpdateTimeDesc(Integer status, Date date);
}
