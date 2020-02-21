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

    private String uniqueId;

    private String domain;

}
