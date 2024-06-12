package com.alivold.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * Jwt工具类
 */
public class JwtUtil {
    //JWT有效期
    public static final Long JWT_TTL = 60 * 60 *1000L; //1000ms * 60 *60 =1小时
   //密钥明文
    public static final String JWT_KEY = "guomingxian";

    public static String getUUID(){
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token;
    }

    /**
     * 生成Jwt
     * subject(json字符串)是token中要存放的数据
     */
    public static String createJwt(String subject){
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    /**
     * 生成Jwt， 带有过期时间
     */
    public static String createJwt(String subject, Long ttlMillis){
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if(ttlMillis == null){
            ttlMillis = JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expireDate = new Date(expMillis);
        return Jwts.builder().setId(uuid).setSubject(subject)
                .setIssuer("guomingxian")//签发者
                .setIssuedAt(now)//签发时间
                .signWith(signatureAlgorithm, secretKey);
              //  .setExpiration(expireDate);
    }

    /**
     * 生成加密后的密钥secretKey
     */
    private static SecretKey generalKey(){
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 解析Jwt
     */
    public static Claims parseJwt(String jwt) throws Exception{
        SecretKey secretKey = generalKey();
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }
}
