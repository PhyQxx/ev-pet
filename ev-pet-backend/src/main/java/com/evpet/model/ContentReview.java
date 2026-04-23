package com.evpet.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("content_reviews")
public class ContentReview {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;     // nickname / post / comment
    private Long userId;
    private String userName;
    private String target;
    private String content;
    private String violation;
    private String reason;
    private String status;   // pending / approved / rejected
    private Long reviewerId;
    private LocalDateTime reviewTime;
    private LocalDateTime createTime;
}
