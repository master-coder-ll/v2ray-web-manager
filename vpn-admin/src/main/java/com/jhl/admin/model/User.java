package com.jhl.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseEntity implements Serializable {
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String nickName;
    @Column(nullable = false)
    private String role;
    @Transient
    private String vCode;
    //邀请码
    @Transient
    private  String inviteCode;
    private Integer status =1;

    private String remark;



}
