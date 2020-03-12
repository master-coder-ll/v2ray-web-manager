package com.jhl.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmailEventHistory extends BaseEntity implements Serializable {

    private String email;

    private String event;

    /**
     * 下次可以发送的时间
     */
    private Date  unlockDate;





}

