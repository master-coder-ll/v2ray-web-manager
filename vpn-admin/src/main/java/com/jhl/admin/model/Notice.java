package com.jhl.admin.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
/**
 * 公告
 */
public class Notice extends BaseEntity implements Serializable {

    private String name;
    private Date toDate;
    @Column( columnDefinition = "TEXT")
    private String content;

    private Integer status =1;


}
