package com.jhl.admin.VO;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class GenerateInviteCodeVO implements Serializable {
    /**
     * 数量
     */
    @Range(min = 1,max = 100,message = "生成邀请码数量需要在[1,100]之间")
    Integer quantity;
    /**
     * 有效时间
     */
    @NotNull
    private Date effectiveTime;

}
