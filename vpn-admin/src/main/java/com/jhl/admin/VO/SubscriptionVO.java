package com.jhl.admin.VO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jhl.admin.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionVO extends BaseEntityVO implements Serializable {
    private String code ;

    private Integer accountId;
}
