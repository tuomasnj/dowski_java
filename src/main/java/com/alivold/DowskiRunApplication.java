package com.alivold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(exclude = { RedisRepositoriesAutoConfiguration.class})
@ServletComponentScan
public class DowskiRunApplication {

    public static void main(String[] args) {
        SpringApplication.run(DowskiRunApplication.class, args);
    }
}