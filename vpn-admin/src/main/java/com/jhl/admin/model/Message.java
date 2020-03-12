package com.jhl.admin.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Message extends BaseEntity implements Serializable {

    @Column
    private String messageContent;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
