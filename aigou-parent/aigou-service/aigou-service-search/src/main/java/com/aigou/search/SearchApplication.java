package com.aigou.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author anchao
 * @date 2020/2/23 14:45
 */
@EnableFeignClients(basePackages = "com.aigou.goods.feign")
@EnableElasticsearchRepositories("com.aigou.search.dao")
@EnableEurekaClient
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class},scanBasePackages = {"com.aigou.*"})//排除数据库链接 实用feign调用goods查询数据
public class SearchApplication {

    public static void main(String[] args) {
        /**
         * Springboot整合Elasticsearch 在项目启动前设置一下的属性，防止报错
         * 解决netty冲突后初始化client时还会抛出异常
         * availableProcessors is already set to [12], rejecting [12]
         ***/
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(SearchApplication.class,args);
    }


}
