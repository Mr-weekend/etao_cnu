package cn.etaocnu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "cn.etaocnu.cloud.system.mapper")
public class ServiceSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSystemApplication.class, args);
    }
}
