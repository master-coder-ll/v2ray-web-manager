package com.jhl.pojo;

import com.jhl.task.TaskCondition;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;


@Slf4j
public  class DelayedTask implements Delayed {
    @Setter
    private  Long nextRunTime =System.currentTimeMillis();
    @Setter
    @Getter
   protected TaskCondition taskCondition = TaskCondition.builder().build();


    @Override
    public long getDelay(TimeUnit unit) {

        return unit.convert(nextRunTime-System.currentTimeMillis(), TimeUnit.MILLISECONDS);

    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.nextRunTime - ((DelayedTask) o).nextRunTime);
    }


}


