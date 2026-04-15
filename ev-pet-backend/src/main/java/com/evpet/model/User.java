package com.evpet.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openId;          // 微信openId
    private String phone;           // 手机号
    private String nickname;         // 昵称
    private String avatar;           // 头像URL
    private Integer gold;            // 金币
    private Integer level;           // 用户等级
    private Integer exp;             // 经验值
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
