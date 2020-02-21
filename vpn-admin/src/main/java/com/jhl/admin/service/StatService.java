package com.jhl.admin.service;

import com.jhl.admin.model.Account;
import com.jhl.admin.model.Stat;
import com.jhl.admin.repository.StatRepository;
import com.jhl.admin.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
@Service
public class StatService {
    @Autowired
    StatRepository statRepository;


    /**
     * 已当天为标准的from date
     * toDate ，from+30= next
     * next 小于maxDate
     * @param account
     * @return
     */
    public Stat createStat(Account account){
        Date today = new Date();
        Stat stat = statRepository.findByAccountIdAndFromDateBeforeAndToDateAfter(account.getId(), today,today);
            if (stat == null){
                Date fromDate =Utils.formatDate(today,null);

                Integer cycleNum = account.getCycle();
                Date maxToDate = account.getToDate();
                Date nextCycleDate = Utils.getDateBy(fromDate,cycleNum, Calendar.DAY_OF_YEAR);
                    if (!maxToDate.after(fromDate)) return  null;
                stat = new Stat();
                 stat.setAccountId(account.getId());
                 stat.setFromDate(fromDate);
                 stat.setToDate(nextCycleDate);
                 stat.setFlow(0l);
                 statRepository.save(stat);
            }
            return stat;

    }
  /*  private void createStat(Account account) {
        Integer accountId = account.getId();
        Date fromDate = new Date();
        Stat stat1 = statRepository.findByAccountIdAndFromDateBeforeAndToDateAfter(accountId, fromDate, fromDate);
        //如果已经存在这个这个周期的返回
        if (stat1 != null) return;

        Page<Stat> statPage = statRepository.findAll(Example.of(
                Stat.builder().accountId(accountId).build()
                ), PageRequest.of(0, 1,
                Sort.by(Sort.Order.desc("id"))
                )
        );

        if (statPage != null && statPage.getSize() > 0) {

            //最新的stat
            Stat stat = statPage.getContent().get(0);
            //上一期的toDate 等于这一期的fromDate
            if (stat.getToDate().after(fromDate)) fromDate = stat.getToDate();
        }
        //to
        Integer cycleNum = account.getCycle();
        Date maxToDate = account.getToDate();
        Date nextCycleDate = Utils.getDateBy(fromDate, cycleNum, Calendar.DAY_OF_YEAR);
        Date toDate = maxToDate.after(nextCycleDate) ? nextCycleDate : maxToDate;

        if (!toDate.after(fromDate)) return;

        Stat stat = Stat.builder().accountId(accountId).build();
        stat.setFromDate(fromDate);
        stat.setToDate(toDate);
        stat.setFlow(0l);
        statRepository.save(stat);
    }*/


}
