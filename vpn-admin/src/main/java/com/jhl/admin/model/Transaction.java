package com.jhl.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_transaction")
public class Transaction extends BaseEntity implements Serializable {

    //交易的类型是 现金 还是通过套餐码
    private Integer transType;

    private Integer transStatus;
    private Long price;
}

