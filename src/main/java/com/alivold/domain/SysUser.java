package com.alivold.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id; //主键

    private String userName; // 用户名

    private String nickName; //昵称

    private String password; //密码

    private String status; // 账号状态（0正常、1停用）

    private String email; //邮箱

    private String phonenumber;//手机号

    private String sex;//用户性别（0男，1女）

    private String userType;//用户类型(0管理员， 1普通用户)

    private String avatar; //头像

    private Long createBy;//创建者的id

    private Date createTime; //创建时间

    private Long updateBy; //更新人员id

    private Date updateTime; //更新时间

    private Integer delFlag; //删除标志
}
