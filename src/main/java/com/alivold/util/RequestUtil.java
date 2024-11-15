package com.alivold.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alivold.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 调用第三方接口
 */

@Component
@Slf4j
public class RequestUtil {
    @Autowired
    private RestTemplate restTemplate;

    public JSONObject openApiRequest(String url, JSONObject params){
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        for(String key : params.keySet()){
            uriComponentsBuilder.queryParam(key, params.getStr(key));
        }
        String fullUrl = null;
        try {
            fullUrl = URLDecoder.decode(uriComponentsBuilder.toUriString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("解码异常", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
        log.info("发送openApi请求：RequestUrl=【{}】", fullUrl);
        String responseStr = restTemplate.getForObject(fullUrl, String.class);
        log.info("接收openApi响应：Response=【{}】", responseStr);
        return JSONUtil.parseObj(responseStr);
    }
}
