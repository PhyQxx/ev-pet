package com.evpet.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkVO {
    private List<WorkItemVO> availableWorks;
    private List<WorkRecordVO> recentRecords;
    private WorkStatsVO stats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkItemVO {
        private Long id;
        private String name;
        private String description;
        private String icon;
        private Integer duration;     // 分钟
        private Integer earnings;    // 金币
        private Integer exp;         // 经验
        private String status;       // available/ongoing/done
        private Long remainingTime;  // 剩余时间(秒)
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkRecordVO {
        private Long id;
        private String name;
        private String icon;
        private Integer earnings;
        private Integer exp;
        private String startTime;
        private String endTime;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkStatsVO {
        private Integer todayEarnings;
        private Integer totalEarnings;
        private Integer totalWorkCount;
    }
}
