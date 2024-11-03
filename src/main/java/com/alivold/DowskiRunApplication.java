package com.alivold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = { RedisRepositoriesAutoConfiguration.class})
@ServletComponentScan
@EnableScheduling
@EnableAsync
public class DowskiRunApplication {

    public static void main(String[] args) {
        SpringApplication.run(DowskiRunApplication.class, args);
    }
}