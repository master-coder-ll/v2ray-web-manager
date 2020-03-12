package com.jhl.admin.VO;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
/**
 * 公告
 */
public class NoticeVO extends BaseEntityVO implements Serializable {

    private String name;
    private Date toDate;
    private String content;

    private Integer status =1;


}
