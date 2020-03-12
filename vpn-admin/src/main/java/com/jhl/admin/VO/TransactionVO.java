package com.jhl.admin.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_transaction")
public class TransactionVO extends BaseEntityVO implements Serializable {

    //交易的类型是 现金 还是通过套餐码
    private Integer transType;

    private Integer transStatus;
    private Long price;
}

