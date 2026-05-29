package com.evpet.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDTO {
    private String code;        // 微信授权码
    private String phone;       // 手机号
    private String verifyCode; // 验证码
    @NotNull(message = "登录方式不能为空")
    private Integer loginType;  // 1-微信 2-手机号
}
