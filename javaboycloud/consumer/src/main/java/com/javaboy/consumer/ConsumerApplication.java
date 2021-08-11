package com.javaboy.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    //提供一个 RestTemplate 的实例
    @Bean
    RestTemplate restTemplateOne() {
        return new RestTemplate();
    }

    //使用 Ribbon 来快速实现负载均衡
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
       return new RestTemplate();
    }
    

}
