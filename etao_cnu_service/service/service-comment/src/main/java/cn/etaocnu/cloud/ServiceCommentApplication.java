package cn.etaocnu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "cn.etaocnu.cloud.comment.mapper")
public class ServiceCommentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCommentApplication.class, args);
    }
}
