package com.jhl.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Entity
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailEventHistory extends BaseEntity implements Serializable {

    private String email;

    private String event;

    /**
     * 下次可以发送的时间
     */
    private Date  unlockDate;





}

