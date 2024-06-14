package com.alivold.service;

import com.alivold.domain.SysUser;
import com.alivold.util.ResponseResult;
import org.springframework.http.ResponseEntity;

public interface LoginService {
    ResponseEntity<ResponseResult> login(SysUser sysUser);

    ResponseResult logout(String token);

    ResponseResult getUserPermissions(String token);
}
