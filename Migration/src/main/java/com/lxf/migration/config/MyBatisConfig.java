package com.lxf.migration.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.lxf.migration.mapper")

public class MyBatisConfig {
}
