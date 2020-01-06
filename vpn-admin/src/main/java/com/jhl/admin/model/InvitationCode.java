package com.jhl.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
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
