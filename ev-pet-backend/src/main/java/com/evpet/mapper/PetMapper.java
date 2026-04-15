package com.evpet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evpet.model.Pet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PetMapper extends BaseMapper<Pet> {
}
