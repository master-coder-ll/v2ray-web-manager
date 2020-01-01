package com.ljh.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowStat {
    private String accountNo;

    //B 字节
    private Long used;

    //上报时候失败的次数
    private  int failureTimes;

    private  String uniqueId;



}
