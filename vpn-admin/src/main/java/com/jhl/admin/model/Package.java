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
    /**
     * 1.正常的包月套餐
     * 2.流量包
     * 3.加速包
     */
    private Integer type;
    /**
     * 1.时
     * 2. 天
     * 3.月
     * 4.年
     */
    private Integer cycleType;
    //周期
    /**
     * 有效时间 = cycleType(时，天，周，月)* cycle;
     */
    private Integer cycle;
    //说明
    private String desc;
    /* @OneToMany
     @JoinColumn(name = "package_id"  )
     private List<PackageCode> packageCodes;*/
    //0 删除 1 正常
    private Integer status;

    private Integer price;


}

