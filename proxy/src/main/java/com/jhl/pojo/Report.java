package com.jhl.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
public  class Report<T> implements Delayed {

    long nextTime;
    private int failureTimes;
    private  T t;
    @Override
    public long getDelay(TimeUnit unit) {
        /**
         * 巨坑
         */
        return unit.convert(nextTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.nextTime - ((Report) o).nextTime);
    }



    public int getFailureTimes() {
        return this.failureTimes;
    }

    public void setFailureTimes(int times) {
        this.failureTimes=times;
    }
    public static void main(String[] args) {

        System.out.println(TimeUnit.NANOSECONDS.convert(6000, TimeUnit.MILLISECONDS));
    }
}
