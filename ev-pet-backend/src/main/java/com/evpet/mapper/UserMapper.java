package com.evpet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evpet.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
