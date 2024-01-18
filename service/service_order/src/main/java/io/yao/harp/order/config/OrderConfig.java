package io.yao.harp.order.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@MapperScan("io.yao.harp.order.mapper")
@Configuration
public class OrderConfig {
}
