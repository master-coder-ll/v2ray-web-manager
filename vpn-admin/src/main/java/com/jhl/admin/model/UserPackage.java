package com.jhl.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserPackage extends BaseEntity implements Serializable {


    private Integer planId;

    private Date  startDate;

    private  Date endDate;

    private  String description;

    private Integer status;

    @Transient
    Package aPackage;

}
