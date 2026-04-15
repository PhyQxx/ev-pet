package com.evpet.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("work_records")
public class WorkRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long petId;
    private String workName;
    private Integer duration;      // 工作时长(秒)
    private Integer earnings;       // 收益金币
    private Integer exp;           // 获得经验
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;          // pending/ongoing/completed
    private LocalDateTime createTime;
}
