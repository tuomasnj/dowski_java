package com.alivold.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // 核心线程数，2核 * 2 = 4
        executor.setMaxPoolSize(8);  // 最大线程数，2核 * 4 = 8
        executor.setQueueCapacity(100); // 队列容量，可根据需求调整
        executor.setThreadNamePrefix("FileUpload-"); // 线程名前缀
        executor.initialize();
        return executor;
    }
}