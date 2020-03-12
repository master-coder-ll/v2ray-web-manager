package com.jhl.admin.VO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jhl.admin.model.BaseEntity;
import com.jhl.admin.model.User;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageVO extends BaseEntityVO implements Serializable {

    private String messageContent;


    private UserVO userVO;



}
