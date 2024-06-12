package com.alivold.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Menu {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String menuName;

    private String permission;

    private Integer status;

    private Integer delFlag;
}
