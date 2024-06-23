package com.alivold.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SysMemo {
    private Long userId;

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    @JsonProperty("day")
    private Date memoDate;

    @JsonProperty("thingToDo")
    private String eventContent;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonProperty("remindTime")
    private Date notifyTime;

    private Integer status;
}
