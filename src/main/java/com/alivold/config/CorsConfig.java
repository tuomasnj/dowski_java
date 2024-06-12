package com.alivold.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        //设置允许跨域的路径
        corsRegistry.addMapping("/**")
                //设置允许Cookie
                .allowCredentials(true)
                //设置允许跨域请求的域名
                .allowedOriginPatterns("*")
                //设置允许的请求方式
                .allowedMethods("POST")
                //设置允许的header
                .allowedHeaders("*")
                //暴露服务端请求头
                .exposedHeaders("*")
                //浏览器缓存预检请求的时间
                .maxAge(3600);
    }
}
