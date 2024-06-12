package com.alivold.controller;

import com.alivold.domain.SysUser;
import com.alivold.service.LoginService;
import com.alivold.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<ResponseResult> login(@RequestBody SysUser sysUser) {
        return loginService.login(sysUser);
    }

    @PostMapping("/logout")
    public ResponseResult logout(@RequestHeader Map<String, String> headers){
        String token = headers.get("token");
        return loginService.logout(token);
    }
}
