package com.evpet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evpet.model.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
