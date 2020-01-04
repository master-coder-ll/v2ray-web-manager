package com.jhl.pojo;

import com.ljh.common.model.FlowStat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ComparableFlowStat extends FlowStat implements Delayed {

    long nextTime;

    @Override
    public long getDelay(TimeUnit unit) {
        /**
         * 巨坑
         */
            return  unit.convert(nextTime-System.currentTimeMillis(), TimeUnit.MILLISECONDS);

    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.nextTime - ((ComparableFlowStat) o).nextTime);
    }

    public static void main(String[] args) {

        System.out.println(TimeUnit.NANOSECONDS.convert(6000, TimeUnit.MILLISECONDS));
    }
}
