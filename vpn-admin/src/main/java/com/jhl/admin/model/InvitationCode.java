package com.jhl.admin.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
/**
 * 公告
 */
public class InvitationCode extends BaseEntity implements Serializable {

    private Integer generateUserId;
    private Integer regUserId;
    @Column(unique = true)
    private String  inviteCode;

    private  Integer status;



}
