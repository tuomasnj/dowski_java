package com.alivold.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alivold.domain.LoginUser;
import com.alivold.exception.BaseException;
import com.alivold.util.JwtUtil;
import com.alivold.util.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;

    //private static final String LOGIN_KEY = "login_user";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("token");
        if(StrUtil.isEmpty(token)){
            //如果token为空放行
            filterChain.doFilter(request, response);
            return;
        }
        //解析token
        Claims claims = null;
        try {
            claims = JwtUtil.parseJwt(token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("token解析异常");
        }
        String userId = claims.getSubject();
        //获取redis中的用户信息
        LoginUser loginUser = redisCache.getCacheObject(token);
        if(ObjectUtil.isNull(loginUser)){
            throw new BaseException("jwt已过期");
        }else{
            //刷新token时间
            redisCache.expire(token, 60* 60);
        }
        //用户信息存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities()));
        filterChain.doFilter(request, response); //放行
    }
}
