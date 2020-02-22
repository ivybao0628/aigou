package com.aigou;

import entity.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author anchao
 * @date 2020/2/18 14:31
 */
@SpringBootApplication
@EnableEurekaClient  //开启Eureka客户端
@MapperScan(basePackages = {"com.aigou.goods.dao"}) //tk mybatis 包扫描
public class GoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class, args);
    }


    /**
     * Id生成器
     * @return
     */
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(0,0);
    }
}
