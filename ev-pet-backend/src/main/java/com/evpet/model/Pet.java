package com.evpet.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pets")
public class Pet {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;             // 主人ID
    private String name;             // 宠物名
    private Integer stage;           // 进化阶段 1/2/3
    private Integer level;           // 等级
    private Integer exp;             // 经验值
    private Integer health;          // 体力 0-100
    private Integer fullness;        // 饱食度 0-100
    private Integer mood;            // 心情 0-100
    private String appearance;        // 装扮外观 JSON
    private LocalDateTime lastFeedTime;   // 最后喂食时间
    private LocalDateTime lastBathTime;   // 最后洗澡时间
    private LocalDateTime lastPlayTime;   // 最后陪玩时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
