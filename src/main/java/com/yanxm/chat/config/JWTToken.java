package com.yanxm.chat.config;

import com.yanxm.chat.pojo.Users;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authc.AuthenticationToken;

@Setter
@Getter
public class JWTToken implements AuthenticationToken {

    // 密钥
    private String token;

    private Users users;

    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return users;
    }

    @Override
    public Object getCredentials() {
        return token;
    }



}
