package io.yao.harp.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2配置信息
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi webApiConfig(){

        return GroupedOpenApi.builder()
                .group("webApi")
                .pathsToMatch("/api/**")
                .build();

//        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("webApi")
//                .apiInfo(webApiInfo())
//                .select()
//                //只显示api路径下的页面
//                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
//                .build();

    }

    @Bean
    public GroupedOpenApi adminApiConfig(){

        return GroupedOpenApi.builder()
                .group("adminApi")
                .pathsToMatch("/admin/**")
                .build();

//        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("adminApi")
//                .apiInfo(adminApiInfo())
//                .select()
//                //只显示admin路径下的页面
//                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
//                .build();

    }


//    private ApiInfo webApiInfo(){
//
//        return new ApiInfoBuilder()
//                .title("网站-API文档")
//                .description("本文档描述了网站微服务接口定义")
//                .version("1.0")
//                .contact(new Contact("atguigu", "http://atguigu.com", "493211102@qq.com"))
//                .build();
//    }

//    private ApiInfo adminApiInfo(){
//
//        return new ApiInfoBuilder()
//                .title("后台管理系统-API文档")
//                .description("本文档描述了后台管理系统微服务接口定义")
//                .version("1.0")
//                .contact(new Contact("atguigu", "http://atguigu.com", "49321112@qq.com"))
//                .build();
//    }
    @Bean
    public OpenAPI testConfig() {
        return new OpenAPI().info(new Info()
                .title("代码平台 API")
                .description("SpringDoc API 演示")
                .version("v1.0.0")
        );
    }

}
