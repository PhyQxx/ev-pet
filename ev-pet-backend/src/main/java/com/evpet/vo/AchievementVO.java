package com.evpet.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementVO {
    private List<AchievementItemVO> achievements;
    private Integer completedCount;
    private Integer totalCount;
    private Integer totalReward;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AchievementItemVO {
        private Long id;
        private String name;
        private String description;
        private String icon;
        private Integer rewardGold;
        private Integer conditionType;
        private Integer conditionValue;
        private Integer currentProgress;
        private Boolean completed;
        private Boolean claimed;
    }
}
