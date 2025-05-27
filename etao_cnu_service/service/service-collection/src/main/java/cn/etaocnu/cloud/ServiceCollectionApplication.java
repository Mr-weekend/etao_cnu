package cn.etaocnu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "cn.etaocnu.cloud.collection.mapper") //import tk.mybatis.spring.annotation.MapperScan;
public class ServiceCollectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCollectionApplication.class, args);
    }
}
