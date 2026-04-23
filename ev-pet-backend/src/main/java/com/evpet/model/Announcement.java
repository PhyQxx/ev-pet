package com.evpet.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("announcements")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String type;      // activity / maintenance / system
    private String target;    // all / specific
    private String status;   // draft / published / withdrawn
    private LocalDateTime createTime;
    private LocalDateTime publishTime;
    private LocalDateTime updateTime;
}
