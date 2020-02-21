package com.jhl.admin.model;

import lombok.*;

import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Package extends BaseEntity implements Serializable {

    private String name;
    //宽带
    private Integer bandwidth;

    private Integer speed;

    private  Integer  connections;
    //周期
    /**
     *0 1 30
     */
    private Integer cycle;
    //说明
    private String description;

    private Integer status;

    private Integer price;

    private  Integer show;
    //间隔
    private  Integer interval;

    /**
     * 计划类型
     * standard, 相同的 plan 应该可以直接叠加，不同的 plan 不应该叠加
     * plus ,可叠加加油包
     */
    private String planType;

}

