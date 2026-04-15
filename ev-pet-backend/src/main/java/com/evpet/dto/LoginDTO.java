package com.evpet.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String code;        // 微信授权码
    private String phone;       // 手机号
    private String verifyCode; // 验证码
    private Integer loginType;  // 1-微信 2-手机号
}
