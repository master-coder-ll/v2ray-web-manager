package com.jhl.admin.VO;

import com.jhl.admin.model.Package;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserPackageVO extends BaseEntityVO implements Serializable {


    private Integer planId;

    private Date  startDate;

    private  Date endDate;

    private  String description;

    private Integer status;

    @Transient
    Package aPackage;

}
