package com.alivold.service;

import cn.hutool.json.JSONObject;
import com.alivold.domain.SysWeather;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public interface LifeService {
    CompletableFuture<List<SysWeather>> queryTempForecastLists(CountDownLatch countDownLatch);

    CompletableFuture<JSONObject> queryCurTemp(CountDownLatch countDownLatch);
}
