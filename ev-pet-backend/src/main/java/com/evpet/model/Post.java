package com.evpet.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("posts")
public class Post {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String content;
    private String image;
    private Integer likes;
    private Integer comments;
    private String status;        // pending/approved/rejected
    private String rejectReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
