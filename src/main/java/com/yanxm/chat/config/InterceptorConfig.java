package com.yanxm.chat.config;

import com.yanxm.chat.handler.JWTInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor())
                .addPathPatterns("/**")          //拦截所有
                .excludePathPatterns("/useradmin/login" /*, ""*/);  //排除用户登录和注册
    }
}
