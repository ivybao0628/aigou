package com.aigou;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author anchao
 * @date 2020/2/22 15:31
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class},scanBasePackages = {"com.aigou.*"})
@EnableEurekaClient
@EnableCanalClient
@EnableFeignClients(basePackages = {"com.aigou.content.feign"})
public class CanalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class,args);
    }


}
