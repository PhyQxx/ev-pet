package com.evpet.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("activities")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String icon;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;   // draft / active / ended
    private Integer participants;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
