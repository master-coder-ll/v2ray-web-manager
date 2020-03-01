package com.jhl.admin.VO;

import lombok.*;

@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChangePasswordVO {

    private  Integer userId;

    private  String oldPassword;

    private  String newPassword;
}
