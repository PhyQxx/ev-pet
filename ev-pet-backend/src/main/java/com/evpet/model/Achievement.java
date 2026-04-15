package com.evpet.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("achievements")
public class Achievement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;              // 成就名
    private String description;      // 描述
    private String icon;             // 图标
    private Integer rewardGold;       // 奖励金币
    private Integer conditionType;   // 条件类型 1-喂食次数 2-陪玩次数 3-等级 4-进化
    private Integer conditionValue;  // 条件值
    private LocalDateTime createTime;
}
