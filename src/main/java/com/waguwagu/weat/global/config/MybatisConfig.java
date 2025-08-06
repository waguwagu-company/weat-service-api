package com.waguwagu.weat.global.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(
        basePackages = "com.waguwagu.weat.domain",
        annotationClass = Mapper.class
)
public class MybatisConfig {

}

