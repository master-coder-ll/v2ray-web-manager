package com.jhl.admin.VO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jhl.admin.model.BaseEntity;
import com.jhl.admin.model.Message;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
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
