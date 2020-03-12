package com.jhl.admin.VO;

import lombok.*;

import java.io.Serializable;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
/**
 * 公告
 */
public class InvitationCodeVO extends BaseEntityVO implements Serializable {

    private Integer generateUserId;
    private Integer regUserId;

    private String  inviteCode;

    private  Integer status;



}
