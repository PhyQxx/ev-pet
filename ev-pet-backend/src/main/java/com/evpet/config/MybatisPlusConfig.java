package com.evpet.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.evpet.mapper")
public class MybatisPlusConfig {
}
