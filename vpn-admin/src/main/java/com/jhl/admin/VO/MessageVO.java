package com.jhl.admin.VO;

import com.jhl.admin.model.MessageReceiver;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageVO extends BaseEntityVO implements Serializable {

    private String messageContent;


    private UserVO userVO;


    private   List<MessageReceiverVO> messageReceivers;


}
