package com.alivold.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_img")
public class PhotoImage {
    private Long id;

    private Long userId;

    private String imageName;

    private String imageUrl;

    private Date createdTime;
}
