package com.evpet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evpet.model.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
