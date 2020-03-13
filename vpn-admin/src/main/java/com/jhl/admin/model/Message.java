package com.jhl.admin.model;

import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.Base64;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Message extends BaseEntity implements Serializable {

    @Column
    private String messageContent;
    @Column(length = 255)
    private String messageType;
    private Long batchNo;



    @OneToMany(mappedBy = "message",cascade = CascadeType.REMOVE)
    private List<MessageReceiver> messageReceivers;



    public enum MessageType{
        NOTICE,WORK_ORDER
    }

    public static void main(String[] args) {
        byte[] decode = Base64.getDecoder().decode("eyJhZGQiOiJob3N0cy5reHN3MjAxOS5jZiIsImFpZCI6IjY0IiwiaG9zdCI6IiIsImlkIjoiNTI0ZmE5NzMtZTUxYS00MDk3LThhNzQtYjc1NzI3ODE0YzdmIiwibmV0Ijoid3MiLCJwYXRoIjoiL3dzLzQ0a2Zvenc6NmFiNTJkODEzOWE0YjQ0ZDRjYjI3Yjc3NzIwZjY3MjgvIiwicG9ydCI6IjQ0MyIsInBzIjoi55S15L+h6IGU6YCaLeebtOi/ni3nvo7lm70tM+WAjSIsInRscyI6InRscyIsInR5cGUiOiJub25lIiwidiI6IjIifQ==");
        System.out.println(new String(decode));
    }

}
