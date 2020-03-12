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
public class PackageCodeVO extends BaseEntityVO implements Serializable {
    private String code;
    //code 的有效时间
    private Date expire;
    //说明
    private String desc;
    /**
     * -1: 失效
     * 0:未使用
     * 1：已经使用
     */
    private Integer status;




}

