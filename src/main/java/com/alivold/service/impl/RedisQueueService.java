package com.alivold.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class RedisQueueService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private ExecutorService executorService;
    // 从队列中获取消息，阻塞式获取
    public String getMessageFromQueue(String queueName) {
        // BRPOP 是阻塞的，直到有消息可用
        return (String) redisTemplate.opsForList().rightPop(queueName);
    }

    // 将消息放入队列
    public void sendMessageToQueue(String queueName, String message) {
        redisTemplate.opsForList().leftPush(queueName, message);
    }

    public void getBatch(){
        while(true){
            String messageFromQueue =  getMessageFromQueue("test:dowski");
            if(messageFromQueue == null){
                log.info("队列为空， 请等待...");
                continue;
            }
            log.info("消费者{} 获取到的数据: {}", Thread.currentThread().getName(), messageFromQueue);
        }
    }

    @PostConstruct
    public void startConsumers() {
        int numberOfConsumers = 5; // 启动 5 个消费者线程
        executorService = Executors.newFixedThreadPool(numberOfConsumers); // 创建固定大小的线程池

        // 启动多个线程来执行 getBatch 方法
        for (int i = 0; i < numberOfConsumers; i++) {
            executorService.submit(() -> getBatch()); // 提交任务
        }
        System.out.println("已启动 " + numberOfConsumers + " 个消费者线程");
    }

    // 在应用关闭时，关闭线程池
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
            log.info("关闭executorService线程池");
        }
    }
}
