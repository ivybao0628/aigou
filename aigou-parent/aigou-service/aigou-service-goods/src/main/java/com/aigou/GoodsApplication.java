package com.aigou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author anchao
 * @date 2020/2/18 14:31
 */
@SpringBootApplication
@EnableEurekaClient  //开启Eureka客户端
@MapperScan(basePackages = {"com.aigou.dao"}) //tk mybatis 包扫描
public class GoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class, args);
    }
}
