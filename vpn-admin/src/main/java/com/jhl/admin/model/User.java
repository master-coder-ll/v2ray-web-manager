package com.jhl.admin.model;

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
public class User extends BaseEntity implements Serializable {
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String nickName;
    @Column(nullable = false)
    private String role;

    private Integer status =1;

    private String remark;

    //mappedBy The value of mappedBy attributes is the name of the class field on the other side of the relationship
    //cascade 当用户删除的时候
     @OneToMany(mappedBy = "user",cascade = {CascadeType.REMOVE},fetch=FetchType.LAZY)
    private List<Message> messages ;

    @Transient
    private String vCode;
    //邀请码
    @Transient
    private  String inviteCode;



}
