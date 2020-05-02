package com.jhl.framework.task;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskCondition {
    /**
     * 失败重试次数
     */
    private int failureTimes;
    /**
     * 最大失败重试次数
     */
    private int maxFailureTimes;
    /**
     * 直到一个时间点
     */
    private long untilTime;

    /**
     * 间隔时间
     */
    private long interval = 0;


    public long computeDelay() {
        //无法预估次数任务,直接返回间隔事件
        if (maxFailureTimes < 0 || untilTime > 0) {
            return interval;
        }
        //失败次数超过限制
        // 其他
        return failureTimes * interval;

    }
}
