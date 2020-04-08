package com.jhl.admin.controller;

import com.jhl.admin.Interceptor.PreAuth;
import com.jhl.admin.VO.InvitationCodeVO;
import com.jhl.admin.VO.UserVO;
import com.jhl.admin.cache.UserCache;
import com.jhl.admin.constant.enumObject.WebsiteConfigEnum;
import com.jhl.admin.model.BaseEntity;
import com.jhl.admin.model.InvitationCode;
import com.jhl.admin.repository.InvitationCodeRepository;
import com.jhl.admin.service.ServerConfigService;
import com.ljh.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class InvitationCodeController {
    @Autowired
    InvitationCodeRepository invitationCodeRepository;
    @Autowired
    UserCache userCache;
    @Autowired
    ServerConfigService serverConfigService;
    /**
     * 生成邀请码
     *
     * @return
     */
    @PreAuth("vip")
    @ResponseBody
    @PostMapping("/invite-code")
    public Result generateInviteCode( @CookieValue(value = UserController.COOKIE_NAME, defaultValue = "") String auth) {

        if (auth == null) throw new NullPointerException("获取不到用户");


        UserVO user = userCache.getCache(auth);
        if ( !user.getRole().equals("admin") && !serverConfigService.checkKey(WebsiteConfigEnum.VIP_CAN_INVITE.getKey())){
                throw  new RuntimeException("管理员不允许用户邀请其他人");
        }
        Integer userId = user.getId();
        long count = invitationCodeRepository.count(Example.of(InvitationCode.builder().generateUserId(userId).status(0).build()));
        if (count>0) throw  new RuntimeException("存在未使用的邀请码。");
        InvitationCode code = InvitationCode.builder().generateUserId(userId).inviteCode(UUID.randomUUID().toString()).build();
            code.setStatus(0);
        invitationCodeRepository.save(code);
        return Result.doSuccess();
    }

    @PreAuth("vip")
    @ResponseBody
    @GetMapping("/invite-code")
    public Result listByUser(@CookieValue(value = UserController.COOKIE_NAME, defaultValue = "") String auth, Integer page, Integer pageSize) {

        if (auth == null) throw new NullPointerException("获取不到用户");
        UserVO user = userCache.getCache(auth);
        Integer userId = user.getId();
        Page<InvitationCode> codes = null;
            codes= invitationCodeRepository.findAll(Example.of(InvitationCode.builder().generateUserId(userId).build()),
                    PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.asc("status"))));

        return Result.buildPageObject(codes.getTotalElements(),  BaseEntity.toVOList(codes.getContent(), InvitationCodeVO.class));
    }

    @PreAuth("admin")
    @ResponseBody
    @DeleteMapping("/invite-code/{codeId}")
    public Result delete(@CookieValue(value = UserController.COOKIE_NAME, defaultValue = "") String auth, @PathVariable Integer codeId) {

        if (auth == null) throw new NullPointerException("获取不到用户");
        if (codeId == null) throw new NullPointerException("id 不能为空");
        UserVO user = userCache.getCache(auth);
        Integer userId = user.getId();
        InvitationCode invitationCode = new InvitationCode();
        invitationCode.setId(codeId);
        invitationCode.setGenerateUserId(userId);
        invitationCodeRepository.delete(invitationCode);
        return Result.doSuccess();
    }
}
