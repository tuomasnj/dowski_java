package com.alivold.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import com.alivold.dao.SysWeatherMapper;
import com.alivold.domain.SysWeather;
import com.alivold.exception.BaseException;
import com.alivold.service.LifeService;
import com.alivold.util.RequestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
public class LifeServiceImpl implements LifeService {
    @Autowired
    private SysWeatherMapper sysWeatherMapper;

    @Value("${openApi.appId}")
    private String appId;

    @Value("${openApi.appSecret}")
    private String appSecret;
    
    @Autowired
    private RequestUtil requestUtil;

    @Override
    @Async(value = "taskExecutor")
    public CompletableFuture<List<SysWeather>> queryTempForecastLists(CountDownLatch countDownLatch) {
        List<SysWeather> sysWeathers;
        try {
            SysWeather sysWeather = new SysWeather();
            LambdaQueryWrapper<SysWeather> sysWeatherLambdaQueryWrapper = new LambdaQueryWrapper<>();
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE, -1);
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            sysWeatherLambdaQueryWrapper.eq(SysWeather::getDate, date.getTime());
            sysWeathers = sysWeatherMapper.selectList(sysWeatherLambdaQueryWrapper);
            //查今天以及之后的天气
            String url = "https://www.mxnzp.com/api/weather/forecast/南京市";
            JSONObject params = new JSONObject();
            params.set("app_id", appId).set("app_secret", appSecret);
            JSONObject resJsonObj = requestUtil.openApiRequest(url, params);
            List<SysWeather> lists = new ArrayList<>();
            lists.add(sysWeathers.get(0));
            if(resJsonObj != null && resJsonObj.getStr("code").equals("1")) {
                JSONArray jsonArray = resJsonObj.getJSONObject("data").getJSONArray("forecasts");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    SysWeather weather = new SysWeather();
                    weather.setDayOfWeek(item.getInt("dayOfWeek"));
                    weather.setDayTemp(Integer.parseInt(item.getStr("dayTemp").substring(0, item.getStr("dayTemp").length() - 1)));
                    weather.setNightTemp(Integer.parseInt(item.getStr("nightTemp").substring(0, item.getStr("nightTemp").length() - 1)));
                    weather.setDayWeather(item.getStr("dayWeather"));
                    weather.setNightWeather(item.getStr("nightWeather"));
                    weather.setDate(DateUtil.parse(item.getStr("date"), "yyyy-MM-dd"));
                    sysWeathers.add(weather);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("服务异常");
        }finally {
            countDownLatch.countDown();
        }
        return CompletableFuture.completedFuture(sysWeathers);
    }

    @Override
    @Async(value = "taskExecutor")
    public CompletableFuture<JSONObject> queryCurTemp(CountDownLatch countDownLatch) {
        String curTemp = "";
        String curWeather = "";
        JSONObject res = new JSONObject();
        try {
            //QPS有限制,每秒钟一次，这里间隔设置为2.5秒
            Thread.sleep(2000);
            JSONObject params = new JSONObject();
            params.set("app_id", appId).set("app_secret", appSecret);
            JSONObject resObj = requestUtil.openApiRequest("https://www.mxnzp.com/api/weather/current/南京市", params);
            log.info("当前实时天气【{}】", resObj);
            if(resObj != null && resObj.getStr("code").equals("1")){
                JSONObject data = resObj.getJSONObject("data");
                curTemp = data.getStr("temp").substring(0, data.getStr("temp").length() - 1);
                curWeather = data.getStr("weather");
                res.set("curTemp", curTemp);
                res.set("curWeather", curWeather);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("服务异常");
        }finally {
            countDownLatch.countDown();
        }
        return CompletableFuture.completedFuture(res);
    }
}
