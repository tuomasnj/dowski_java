package com.alivold.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@TableName(value = "sys_weather_forecast")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysWeather {
    private Date date;

    private Integer dayOfWeek;

    private Integer dayTemp;

    private Integer nightTemp;

    private String dayWeather;

    private String nightWeather;
}
