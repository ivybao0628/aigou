package com.aigou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author anchao
 * @date 2020/2/18 13:23
 */
@SpringBootApplication
@EnableEurekaServer //开启Eureka服务
public class EurekaApplication {

    /**
     * 加载启动类 以启动类为当前Springboot的标准默认配置
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class,args);
    }
}
