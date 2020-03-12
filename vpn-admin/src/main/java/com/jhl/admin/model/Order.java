package com.jhl.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_order")
@Data
public class Order extends BaseEntity implements Serializable {
    @Column(unique = true)
    private String orderNo;
    private Integer packageId;
    private Integer price;
    //过期时间
    private Date  overdueDate;
    private Integer status;

    private Integer accountId;
}

