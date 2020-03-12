package com.jhl.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message extends BaseEntity implements Serializable {

    @Column
    private String messageContent;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
