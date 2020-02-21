package com.jhl.admin.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PackageCode extends BaseEntity implements Serializable {
    @Column(unique = true)
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

