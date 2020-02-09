package com.atguigu.gmall.order;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author lzzzzz
 * @create 2020-02-07 21:30
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GmallOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(GmallOrderApplication.class, args);
    }
}
