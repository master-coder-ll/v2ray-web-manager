package com.jhl.admin.VO;

import lombok.*;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageVO extends BaseEntityVO implements Serializable {

    private String messageContent;


    private UserVO userVO;



}
