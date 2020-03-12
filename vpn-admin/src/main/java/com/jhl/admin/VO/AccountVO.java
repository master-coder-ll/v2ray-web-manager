package com.jhl.admin.VO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jhl.admin.model.BaseEntity;
import com.jhl.admin.model.Server;
import com.jhl.admin.model.Stat;
import com.jhl.admin.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountVO extends BaseEntityVO implements Serializable {
    @Transient
    public final static int G =1024;
    //有效周期
    private Date fromDate;
    private Date toDate;
    //周期类型 day,week month year？
    private Integer cycle;

    private String accountNo;
    //Kb 1024*1024 =1M
    private Long speed;
    //M，每个周期的流量数
    private Integer bandwidth;
    /*//账号类型
    private  Integer type;*/
    //服务器账号相关
    private String content;
    private Integer status;
    private Integer userId;
    private Integer serverId;
    //单账号最大连接数
    private Integer maxConnection;
    /**
     * 0~9
     * 9最大，上一级应该可以获取下一级的数据
     */
    private Short level;

    private  String subscriptionUrl;

    /**
     * v2rayAccount中的id
     */
    private String uuid;
    //最近的流量统计
    private StatVO statVO;
    private ServerVO serverVO;
    private UserVO userVO;





}

