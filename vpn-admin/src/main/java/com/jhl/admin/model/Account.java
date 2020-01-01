package com.jhl.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account extends BaseEntity implements Serializable {
    @Transient
    public final static int G =1024;
    //有效周期
    private Date fromDate;
    private Date toDate;
    //周期类型 day,week month year？
    private Integer cycle;

    @Column(unique = true,nullable = false)
    private String accountNo;
    //Kb 1024*1024 =1M
    private Long speed;
    //M，每个周期的流量数
    private Integer bandwidth;
    /*//账号类型
    private  Integer type;*/
    //服务器账号相关
    @Column(length = 512)
    private String content;
    private Integer status;
    private Integer userId;
    private Integer serverId;
    //单账号最大连接数
    private Integer maxConnection;
    @Transient
    //最近的流量统计
    private Stat stat;
    @Transient
    private Server server;
    @Transient
    private String email;





}

