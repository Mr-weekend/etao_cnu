package cn.etaocnu.cloud.config.swagger3;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Swagger3Config {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("易淘重师 API")
                        .version("1.0")
                        .description("易淘重师接口文档")
                        .contact(new Contact()
                                .name("etaocnu")
                                .email("zhuyaojian02@163.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8001/user-api/").description("用户端服务接口"),
                        new Server().url("http://localhost:8001/system-api/").description("管理端服务接口")
                ));
    }
//
//    @Bean
//    public GroupedOpenApi clientApi() {
//        return GroupedOpenApi.builder()
//                .group("客户端服务接口")
//                .packagesToScan("cn.etaocnu.cloud.user.controller")
//                .pathsToMatch("/user-api/**")  // 匹配所有客户端路径
//                .build();
//    }

    @Bean
    public GroupedOpenApi clientApi() {
        return GroupedOpenApi.builder()
                .group("客户端服务接口")
                .packagesToScan("cn.etaocnu.cloud.user.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("管理端服务接口")
                .packagesToScan("cn.etaocnu.cloud.system.controller")
                .build();
    }
}