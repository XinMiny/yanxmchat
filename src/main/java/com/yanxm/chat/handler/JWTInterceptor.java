package com.yanxm.chat.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanxm.chat.config.JWTToken;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;

public class JWTInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求头中得token
        String token = request.getHeader("token");
        HashMap<String , Object> map = new HashMap<>();
        try{
            //2. 如果客户端没有携带token，拦下请求
            if(null==token||"".equals(token)){
                map.put("msg","Token无效，您无权访问该接口");
                throw new Exception();
            }
            //3. 如果有，对进行进行token验证
            JWTToken jwtToken = new JWTToken(token);
            SecurityUtils.getSubject().login(jwtToken);
            return true;
        }catch (Exception e){
            String json = new ObjectMapper().writeValueAsString(map);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.println(json);
            writer.close();
            return false;
        }
    }
}
