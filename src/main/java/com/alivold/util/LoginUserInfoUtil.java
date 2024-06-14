package com.alivold.util;

import com.alivold.domain.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LoginUserInfoUtil {
    @Autowired
    private RedisCache redisCache;

    public String getLoginUserId(){
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        return userId;
    }

    public LoginUser getLoginUser(){
        LoginUser loginUser= (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loginUser;
    }
}
