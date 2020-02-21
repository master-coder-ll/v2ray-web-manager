package com.jhl.admin.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@ToString(callSuper = true)
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

