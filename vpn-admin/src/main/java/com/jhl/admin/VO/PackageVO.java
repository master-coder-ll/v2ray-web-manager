package com.jhl.admin.VO;

import com.jhl.admin.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PackageVO extends BaseEntityVO implements Serializable {

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

