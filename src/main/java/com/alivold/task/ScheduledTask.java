package com.alivold.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alivold.dao.MemoMapper;
import com.alivold.dao.SysUserMapper;
import com.alivold.dao.SysWeatherMapper;
import com.alivold.domain.SysMemo;
import com.alivold.domain.SysUser;
import com.alivold.domain.SysWeather;
import com.alivold.exception.BusinessException;
import com.alivold.service.EmailService;
import com.alivold.util.LoginUserInfoUtil;
import com.alivold.util.RequestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ScheduledTask {
    @Autowired
    private MemoMapper memoMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private LoginUserInfoUtil loginUserInfoUtil;

    @Autowired
    private RequestUtil requestUtil;

    @Value("${openApi.appId}")
    private String appId;

    @Value("${openApi.appSecret}")
    private String appSecret;

    @Autowired
    private SysWeatherMapper sysWeatherMapper;

    /**
     * 发送备忘录通知到邮箱
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void sendNotice() {
        // String time =  HH:mm:ss");
        //  log.info("现在是北京时间【{}】", time);
        LambdaQueryWrapper<SysMemo> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(SysMemo::getNotifyTime, new Date());
        wrapper.eq(SysMemo::getStatus, 0);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        wrapper.eq(SysMemo::getMemoDate, today.getTime());
        List<SysMemo> sysMemos = memoMapper.selectList(wrapper);
        for (SysMemo s : sysMemos) {
            s.setStatus(1);
            try {
                LambdaQueryWrapper<SysMemo> memoWrapper = new LambdaQueryWrapper<>();
                memoWrapper.eq(SysMemo::getUserId, s.getUserId());
                memoWrapper.eq(SysMemo::getMemoDate, today.getTime());
                memoMapper.update(s, memoWrapper);
                //查事件对应的用户
                SysUser sysUser = sysUserMapper.selectById(s.getUserId());
                //发送邮件通知
                emailService.sendRemindEmail1(sysUser.getEmail(), DateUtil.format(new Date(), "yyyy年M月d日") + "备忘事项提醒",
                        "【" + DateUtil.format(new Date(), "yyyy年M月d日") + "】" + "待办", s.getEventContent());
                log.info("通知了内容为【{}】的备忘事项", s.getEventContent());
            } catch (Exception e) {
                log.error("【{}】", e.getMessage());
                throw new BusinessException("发送邮件通知异常");
            }
        }
    }

    /**
     * 每天晚上11点记录当天的天气预报信息
     */
    @Transactional
    @Scheduled(cron = "0 0 23 * * ?")
    public void markTodayTemperature(){
        try {
            String url = "https://www.mxnzp.com/api/weather/forecast/南京市";
            JSONObject params = new JSONObject();
            params.set("app_id", appId).set("app_secret", appSecret);
            JSONObject resJsonObj = requestUtil.openApiRequest(url, params);
            if(resJsonObj != null && resJsonObj.getStr("code").equals("1")){
                JSONArray jsonArray = resJsonObj.getJSONObject("data").getJSONArray("forecasts");
                JSONObject obj = (JSONObject)jsonArray.get(0);
                log.info("当日天气预报：【{}】", JSONUtil.toJsonStr(obj));
                SysWeather sysWeather = new SysWeather();
                LambdaQueryWrapper<SysWeather> sysWeatherLambdaQueryWrapper = new LambdaQueryWrapper<>();
                Calendar date = Calendar.getInstance();
                date.set(Calendar.HOUR_OF_DAY, 0);
                date.set(Calendar.MINUTE, 0);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                sysWeatherLambdaQueryWrapper.eq(SysWeather::getDate, date.getTime());
                List<SysWeather> sysWeathers = sysWeatherMapper.selectList(sysWeatherLambdaQueryWrapper);
                if(sysWeathers != null && sysWeathers.size() > 0){
                    log.info("记录已存在，无需重复更新");
                }else {
                    sysWeather.setDate(new Date());
                    //去掉℃符号
                    sysWeather.setDayTemp(Integer.parseInt(obj.getStr("dayTemp").substring(0, obj.getStr("dayTemp").length() -1)));
                    sysWeather.setNightTemp(Integer.parseInt(obj.getStr("nightTemp").substring(0, obj.getStr("nightTemp").length() -1)));
                    sysWeather.setDayOfWeek(obj.getInt("dayOfWeek"));
                    sysWeather.setDayWeather(obj.getStr("dayWeather"));
                    sysWeather.setNightWeather(obj.getStr("nightWeather"));
                    sysWeatherMapper.insert(sysWeather);
                }
            }else{
                log.error("接口返回数据异常");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException("更新当日天气数据失败");
        }

    }
}
