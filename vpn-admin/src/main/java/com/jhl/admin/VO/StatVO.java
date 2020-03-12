package com.jhl.admin.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StatVO extends BaseEntityVO implements Serializable {

    private Integer accountId;
    //结算周期
    private Date fromDate;

    private Date toDate;
    //流量 最大值 2^64 ,1T 等于 2^40
    private Long flow;
}

