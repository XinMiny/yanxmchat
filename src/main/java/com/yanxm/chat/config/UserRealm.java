package com.yanxm.chat.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yanxm.chat.pojo.Users;
import com.yanxm.chat.service.UsersService;
import com.yanxm.chat.utils.JWTUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UsersService usersService;

    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        Users principal = (Users) auth.getPrincipal();
        if (principal == null) {
            // 解密获得username，用于和数据库进行对比
            DecodedJWT jwt = JWT.decode(token);
            String username = jwt.getClaim("username").asString();
            if (username == null) {
                throw new AuthenticationException(" token错误，请重新登入！");
            }

            principal = usersService.queryUsernameIsExist(username);
            if (principal == null) {
                throw new AccountException("账号不存在!");
            }

        }
        if(JWTUtil.isExpire(token)){
            throw new AuthenticationException(" token过期，请重新登入！");
        }
        if (JWTUtil.verifyToken(token, principal.getPassword()) == null) {
            throw new CredentialsException("密码错误!");
        }


        return new SimpleAuthenticationInfo(principal, token, getName());
    }
}
