package com.sky7.shiro;

import com.sky7.domain.User;
import com.sky7.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRealm extends AuthorizingRealm {
    /**
     * 执行授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权逻辑");

        //给资源授权，SimpleAuthorizationInfo为AuthorizationInfo接口的其中一个实现类
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        //添加资源的授权字符串
        //simpleAuthorizationInfo.addStringPermission("user:add");

        //到数据库查询当前登录用户的授权字符串
        Subject subject = SecurityUtils.getSubject();

        //通过执行认证逻辑中的SimpleAuthenticationInfo()方法的第一个参数传递
        User user = (User)subject.getPrincipal();
        User user1 = userService.findByName(user.getName());
        simpleAuthorizationInfo.addStringPermission(user1.getPerms());

        return simpleAuthorizationInfo;
    }

    @Autowired
    private UserService userService;

    /**
     * 执行认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证逻辑");


        //编写Shiro判断逻辑
        //1、判断用户名
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        User user = userService.findByName(usernamePasswordToken.getUsername());
//        System.out.println(user);
        if(user == null) {
            return null; //Shiro底层会抛出UnknownAccountException
        }

        //2、判断密码
        return new SimpleAuthenticationInfo(user, user.getPassword(), "");

    }
}
