package com.jhl.admin.VO;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class UserVO extends BaseEntityVO implements Serializable {
    private String email;
    private String password;
    private String nickName;
    private String role;

    private Integer status =1;

    private String remark;


    private List<MessageVO> messagesVO ;

    private String vCode;
    //邀请码
    private  String inviteCode;


}
