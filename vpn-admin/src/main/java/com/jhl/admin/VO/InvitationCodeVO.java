package com.jhl.admin.VO;

import lombok.*;

import java.io.Serializable;
import java.util.Date;


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
    private String userName;
    private String  inviteCode;
    private Date effectiveTime;
    private  Integer status;



}
