package com.jhl.admin.VO;

import com.jhl.admin.model.BaseEntity;
import com.jhl.admin.model.Message;
import com.jhl.admin.model.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageReceiverVO extends BaseEntityVO implements Serializable {

    private UserVO user;

    private MessageVO message;
    /**
     * 已经接收？
     */
    private boolean received;


}
