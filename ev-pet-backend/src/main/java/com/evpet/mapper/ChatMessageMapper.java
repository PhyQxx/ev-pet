package com.evpet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evpet.model.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
