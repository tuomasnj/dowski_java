package com.alivold.controller;

import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alivold.domain.SysWeather;
import com.alivold.exception.BaseException;
import com.alivold.exception.BusinessException;
import com.alivold.service.LifeService;
import com.alivold.util.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping("/lifeservice")
@Slf4j
public class LifeServiceController {
    @Autowired
    private LifeService lifeService;

    @PostMapping("/temp/forecast")
    public ResponseResult getTemperatureForecast(){
        try {
            JSONObject resObject = new JSONObject();
            CountDownLatch countDownLatch = new CountDownLatch(2); //使用countDownlatch进行异步任务控制，每一个任务完成以后latch就减1。所有任务完成以后返回结果
            CompletableFuture<List<SysWeather>> future1 =  lifeService.queryTempForecastLists(countDownLatch);
            CompletableFuture<Integer> future2 = lifeService.queryCurTemp(countDownLatch);
            countDownLatch.await();
            resObject.set("list", future1.get());
            resObject.set("curTemp", future2.get());
            return ResponseResult.success(resObject);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("服务异常");
        }
    }
}
