package com.evpet.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatDTO {
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 500, message = "消息长度不能超过500字")
    private String content;     // 消息内容
}
