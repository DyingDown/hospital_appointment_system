package io.yao.harp.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("io.yao.harp.user.mapper")
public class UserConfig {
}
