package com.alivold.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alivold.domain.LoginUser;
import com.alivold.domain.SysUser;
import com.alivold.exception.BaseException;
import com.alivold.service.LoginService;
import com.alivold.util.JwtUtil;
import com.alivold.util.LoginUserInfoUtil;
import com.alivold.util.RedisCache;
import com.alivold.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private LoginUserInfoUtil loginUserInfoUtil;

    @Override
    public ResponseEntity<ResponseResult> login(SysUser sysUser) {
        //进行用户认证
        Authentication authenticate = null;
        try{
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(sysUser.getUserName(), sysUser.getPassword());
            //authenticate是Authentication类型的，验证成功以后，authenticate携带了更为丰富的用户详细信息以及权限信息。
            authenticate = authenticationManager.authenticate(authenticationToken);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException("服务异常");
        }
        //如果用户认证失败，提示
        if(ObjectUtil.isNull(authenticate)){
            return new ResponseEntity(ResponseResult.fail(), new HttpHeaders(), HttpStatus.OK);
        }
        //获取用户信息，根据userid生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        Long userId = loginUser.getUser().getId();
        String jwt = JwtUtil.createJwt(userId.toString());

        //存入redis
        redisCache.setCacheObject(jwt, loginUser, 60* 60);
        HttpHeaders headers = new HttpHeaders();
        headers.add("apiToken", jwt);
        return new ResponseEntity(ResponseResult.success(), headers, HttpStatus.OK);
    }

    @Override
    public ResponseResult logout(String token) {
        //获取SecurityContextHolder中的用户id
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        String userId = loginUser.getUser().getId().toString();

        //删除redis中的用户信息
        redisCache.deleteObject(token);
        return ResponseResult.success();
    }
}
