package com.jhl.admin.VO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jhl.admin.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailEventHistoryVO extends BaseEntityVO implements Serializable {

    private String email;

    private String event;

    /**
     * 下次可以发送的时间
     */
    private Date  unlockDate;





}

