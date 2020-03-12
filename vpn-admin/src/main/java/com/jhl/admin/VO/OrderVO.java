package com.jhl.admin.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_order")
@Data
public class OrderVO extends BaseEntityVO implements Serializable {
    private String orderNo;
    private Integer packageId;
    private Integer price;
    //过期时间
    private Date  overdueDate;
    private Integer status;

    private Integer accountId;
}

