package com.yanxm.chat.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

public class JWTUtil {



    /**
     * 生成token
     * @return
     */
    public static String getToken(Map<String , String> map , String sign){
        JWTCreator.Builder builder = JWT.create();
        Calendar ins = Calendar.getInstance();
        ins.add(Calendar.MINUTE , 30);   //30分钟过期
        map.forEach((k , v)->{
            builder.withClaim(k ,v);
        });

        String token = builder.withExpiresAt(ins.getTime())    //指定过期时间
                .sign(Algorithm.HMAC256(sign));//签名

         return token;
    }

    /**
     * 验证token , 并返回DecodedJWT信息
     */
    public static DecodedJWT verifyToken(String token , String sign){
        return JWT.require(Algorithm.HMAC256(sign)).build().verify(token);
    }

    /**
     * 判断是否过期
     * @param token
     * @return
     */
    public static boolean isExpire(String token){
        DecodedJWT jwt = JWT.decode(token);
        return System.currentTimeMillis()>jwt.getExpiresAt().getTime();
    }
}
