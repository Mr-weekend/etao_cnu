package cn.etaocnu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "cn.etaocnu.cloud.chat.mapper")
public class ServiceChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceChatApplication.class, args);
    }
} 