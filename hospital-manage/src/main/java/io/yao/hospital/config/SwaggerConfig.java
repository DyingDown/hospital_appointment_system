package io.yao.hospital.config;

//import com.google.common.base.Predicates;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger2配置信息
 * @author qy
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi adminApiConfig() {

        return GroupedOpenApi.builder()
                .group("webApi")
                .pathsToExclude("/P2P/**", "/error.*")
                .build();
    }


//    @Bean
//    public Docket webApiConfig(){
//
//        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("webApi")
//                //过滤掉admin路径下的所有页面
//                .paths(Predicates.and(PathSelectors.regex("/P2P/.*")))
//                //过滤掉所有error或error.*页面
//                //.paths(Predicates.not(PathSelectors.regex("/error.*")))
//                .build();
//
//    }

    @Bean
    public OpenAPI testConfig() {
        Contact contact = new Contact();
        contact.setName("o_oyao");
        contact.setEmail("o_oyao@outlook.com");
        contact.setUrl("https://dyingdown.github.io");
        return new OpenAPI().info(new Info()
                .title("网站-API文档")
                .description("本文档描述了网站微服务接口定义")
                .version("v1.0.0")
                .contact(contact)
        );
    }

//    private ApiInfo webApiInfo(){
//
//        return new ApiInfoBuilder()
//                .title("网站-API文档")
//                .description("本文档描述了网站微服务接口定义")
//                .version("1.0")
//                .contact(new Contact("qy", "http://atguigu.com", "55317332@qq.com"))
//                .build();
//    }



}
