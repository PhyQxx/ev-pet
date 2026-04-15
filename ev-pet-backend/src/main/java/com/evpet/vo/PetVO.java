package com.evpet.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetVO {
    private Long id;
    private String name;
    private Integer stage;       // 1-蛋 2-幼年 3-成年
    private Integer level;
    private Integer exp;
    private Integer health;      // 体力
    private Integer fullness;    // 饱食度
    private Integer mood;        // 心情
    private String status;       // 当前状态 happy/hungry/tired/angry
    private String appearance;   // 装扮
    private Long nextEvolutionExp; // 进化所需经验
}
