package com.jhl.admin.VO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jhl.admin.model.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * 公告
 */
public class InvitationCodeVO extends BaseEntityVO implements Serializable {

    private Integer generateUserId;
    private Integer regUserId;

    private String  inviteCode;

    private  Integer status;



}
