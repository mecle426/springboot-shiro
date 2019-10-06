# springboot-shiro
# Spring Boot + Shiro项目搭建

## 一、Spring Boot快速入门

### 1.1 创建Maven Web项目

![image-20191005093033112](/Users/chendan/Documents/image-20191005093033112.png)

​																		项目创建的目录结构

![image-20191006095610732](/Users/chendan/Library/Application Support/typora-user-images/image-20191006095610732.png)

​																		项目的完整目录结构

### 1.2 修改pox.xml

```xml
<!--继承spring-boot的默认父工程-->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.0.RELEASE</version>
</parent>
```

```xml
 <!--导入web支持：SpringMVC开发支持，Servlet相关程序-->
 <dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-web</artifactId>
 </dependency>
```

### 1.3 编写测试Controller类

```java
package com.sky7.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        System.out.println("UserController.hello()");
        return "hello";
    }
}

```

### 1.4 编写Spring Boot启动类

注意：1、启动类只能放在同一包下才能进行映射

​			2、启动项目即启动此启动类，启动成功会有如下日志

```java
package com.sky7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

```

![image-20191005115720127](/Users/chendan/Library/Application Support/typora-user-images/image-20191005115720127.png)

![image-20191005125401197](/Users/chendan/Library/Application Support/typora-user-images/image-20191005125401197.png)

### 1.5 导入thymeleaf模板

#### 	1.5.1 导入thymeleaf依赖

```xml
<!--导入thymeleaf依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

#### 	1.5.2 在Controller中添加testThymeleaf()方法

```java
package com.sky7.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        System.out.println("UserController.hello()");
        return "hello";
    }

    @RequestMapping("/testThymeleaf")
    public String testThymeleaf(Model model) {
        model.addAttribute("name", "Hello Thymeleaf!");
        return "test";
    }
}

```

#### 	1.5.3 在resource中创建templates文件夹并在文件夹中创建test.html文件

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <h2 th:text="${name}"></h2>
</body>
</html>
```

#### 	1.5.4运行Spring Boot启动类

![image-20191005131200532](/Users/chendan/Library/Application Support/typora-user-images/image-20191005131200532.png)

## 二、Spring Boot与Shiro整合实现用户认证

### 2.1 分析Shiro的核心API

​	Subject： 用户主体(把操作交给SecurityManger)

​	SecurityManager： 安全管理器(关联Realm)

​	Realm： Shiro连接数据的桥梁

### 2.2 导入Shiro整合Spring依赖

#### 	2.2.1 修改pom.xml

```xml
<!--导入Shiro整合Spring依赖-->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring</artifactId>
    <version>1.4.0</version>
</dependency>
```

### 2.3 自定义Realm类(UserRealm)

```java
package com.sky7.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class UserRealm extends AuthorizingRealm {
    /**
     * 执行授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权逻辑");
        return null;
    }

    /**
     * 执行认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证逻辑");
        return null;
    }
}
```

### 2.4编写ShiroConfig配置类(*)

```java
package com.sky7.shiro;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Shiro配置类
 */
@Configuration
public class ShiroConfig {
    /**
     * 创建ShiroFilterFactoryBean
     */
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager")DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean;
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        return shiroFilterFactoryBean;
    }


    /**
     * 创建DefaultWebSecurityManager
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm")UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(userRealm);
        //关联Realm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 创建Realm
     */
    @Bean(name = "userRealm")
    public UserRealm getRealm() {
        return new UserRealm();
    }
}
```

### 2.5 使用Shiro内置过滤器实现页面拦截

```java
package com.sky7.shiro;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * Shiro配置类
 */
@Configuration
public class ShiroConfig {
    /**
     * 创建ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager")DefaultWebSecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        /**
         *Shiro内置过滤器，可以实现权限相关的拦截器
         * 常用过滤器：
         *   anon: 无需认证（登录）可以访问
         *   authc: 必须认证才可以访问
         *   user: 如果使用rememberMe的功能可以访问
         *   perms: 该资源必须得到资源权限才可以访问
         *   role: 该资源必须得到角色权限才可以访问
         *
         */
        Map<String, String> filterMap = new LinkedHashMap<String, String>();
        //filterMap.put("/user/add", "authc");
        //filterMap.put("/user/update", "authc");
        filterMap.put("/testThymeleaf", "anon");

        //filterMap.put("/*", "authc");
        filterMap.put("/user", "authc");


        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        //设置拦截后跳转的路径
        shiroFilterFactoryBean.setLoginUrl("/toLogin");

        return shiroFilterFactoryBean;
    }


    /**
     * 创建DefaultWebSecurityManager
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm")UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(userRealm);
        //关联Realm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 创建Realm
     */
    @Bean(name = "userRealm")
    public UserRealm getRealm() {
        return new UserRealm();
    }
}
```

### 2.6 实现用户认证(登录)操作

#### 2.6.1 设计登录页面

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>登录</title>
</head>
<body>
    <h3>登录</h3>
    <h3 th:text="${msg}" style="color: red;"></h3>
    <form method="post" action="login">
        用户名：<input type="text" name="name"><br/>
        密码:<input type="password" name="password"><br/>
        <input type="submit">
    </form>
</body>
</html>
```

#### 2.6.2 编写Controller的登录逻辑

```java
@RequestMapping("/login")
public String login(String name, String password, Model model) {
    /**
     * 使用Shiro编写认证操作
     */
    //1、获取Subject
    org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
    //2、封装用户数据
    UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(name, password);
    //3、执行登录方法
    try {
        subject.login(usernamePasswordToken);
        return "redirect:/testThymeleaf";
    } catch (UnknownAccountException e) {
        //登录失败：用户名不存在
        model.addAttribute("msg", "用户名不存在");
        return "login";
    } catch (IncorrectCredentialsException e) {
        model.addAttribute("msg", "密码错误");
        return "login";
    }
}
```

#### 2.6.3 编写Realm的判断逻辑

```java
package com.sky7.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class UserRealm extends AuthorizingRealm {
    /**
     * 执行授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权逻辑");
        return null;
    }

    /**
     * 执行认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证逻辑");

        //模拟数据库账号密码
        String name = "mecle";
        String password = "123456";

        //编写Shiro判断逻辑
        //1、判断用户名
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        if(!usernamePasswordToken.getUsername().equals(name)) {
            return null; //Shiro底层会抛出UnknownAccountException
        }

        //2、判断密码
        return new SimpleAuthenticationInfo("", password, "");

    }
}
```

### 2.7 整合Mybatis实现登录

#### 	2.7.1 导入Mybatis相关依赖

```xml
<!--导入Mybatis相关依赖-->
<!--连接池-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.0.9</version>
</dependency>
<!--mysql-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<!--SpringBoot的Mybatis启动器-->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.1.1</version>
</dependency>
```

#### 	2.7.2 配置application.properties

位置：src/main/resource目录下

```properties
spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/spring-boot
spring.datasource.username=填写自己的数据库用户名
spring.datasource.password=填写自己的数据库密码

spring.datasource.type=com.alibaba.druid.pool.DruidDataSource

mybatis.type-aliases-package=com.sky7.domain
```

#### 	2.7.3 编写User实体

```java
package com.sky7.domain;

public class User {
    private Integer id;
    private String name;
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
```

#### 	2.7.4 编写UserDao接口

```java
package com.sky7.dao;

import com.sky7.domain.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface UserDao {
		//通过用户名查询用户信息
    @Select({"select * from user where name = #{name}"})
    public User findByName(String name);
}
```

#### 	2.7.5 编写业务接口和实现

接口：

```java
package com.sky7.service;

import com.sky7.domain.User;

public interface UserService {
    public User findByName(String name);
}
```

实现类：

```java
package com.sky7.service.Impl;

import com.sky7.dao.UserDao;
import com.sky7.domain.User;
import com.sky7.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User findByName(String name) {
        return userDao.findByName(name);
    }
}
```

#### 	2.7.6 在启动类中添加@MapperScan注解

```java
package com.sky7;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sky7.dao")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### 	2.7.7 修改UserRealm

```java
package com.sky7.shiro;

import com.sky7.domain.User;
import com.sky7.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRealm extends AuthorizingRealm {
    /**
     * 执行授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权逻辑");
        return null;
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
        return new SimpleAuthenticationInfo("", user.getPassword(), "");

    }
}
```

## 三、 Spring Boot与Shiro整合实现用户授权

### 3.1 使用Shiro内置过滤器拦截资源

#### 	3.1.1 修改ShiroConfig

```java
package com.sky7.shiro;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * Shiro配置类
 */
@Configuration
public class ShiroConfig {
    /**
     * 创建ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager")DefaultWebSecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        /**
         *Shiro内置过滤器，可以实现权限相关的拦截器
         * 常用过滤器：
         *   anon: 无需认证（登录）可以访问
         *   authc: 必须认证才可以访问
         *   user: 如果使用rememberMe的功能可以访问
         *   perms: 该资源必须得到资源权限才可以访问
         *   role: 该资源必须得到角色权限才可以访问
         *
         */
        Map<String, String> filterMap = new LinkedHashMap<String, String>();

        filterMap.put("/user/add","perms[user:add]");
        filterMap.put("/user/*", "authc");


        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        //设置拦截后跳转的路径
        shiroFilterFactoryBean.setLoginUrl("/toLogin");
        shiroFilterFactoryBean.setUnauthorizedUrl("/unAuth");

        return shiroFilterFactoryBean;
    }


    /**
     * 创建DefaultWebSecurityManager
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm")UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(userRealm);
        //关联Realm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 创建Realm
     */
    @Bean(name = "userRealm")
    public UserRealm getRealm() {
        return new UserRealm();
    }
}
```

#### 	3.1.2 添加未授权页面访问Controller

```java
@RequestMapping("/unAuth")
public String unAuth() {
    return "/unAuth";
}
```

#### 	3.1.3 添加未授权页面

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>未授权</title>
</head>
<body>
亲，您未授权该页面访问！
</body>
</html>
```

### 3.2 完成Shiro的资源授权

修改Realm

```java
package com.sky7.shiro;

import com.sky7.domain.User;
import com.sky7.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRealm extends AuthorizingRealm {
    /**
     * 执行授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权逻辑");

        //给资源授权
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        //添加资源的授权字符串
        simpleAuthorizationInfo.addStringPermission("user:add");

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
        return new SimpleAuthenticationInfo("", user.getPassword(), "");

    }
}
```

### 3.3 通过数据库授权字符串对资源授权

#### 	3.3.1 建立数据库

![image-20191006094915797](/Users/chendan/Library/Application Support/typora-user-images/image-20191006094915797.png)

#### 	3.3.2 修改domain实体类

```java
package com.sky7.domain;

public class User {
    private Integer id;
    private String name;
    private String password;
    private String perms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", perms='" + perms + '\'' +
                '}';
    }
}
```

​	3.3.3 修改Realm

```java
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
```

​	3.3.4 修改ShiroConfig配置类

```java
package com.sky7.shiro;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * Shiro配置类
 */
@Configuration
public class ShiroConfig {
    /**
     * 创建ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager")DefaultWebSecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        /**
         *Shiro内置过滤器，可以实现权限相关的拦截器
         * 常用过滤器：
         *   anon: 无需认证（登录）可以访问
         *   authc: 必须认证才可以访问
         *   user: 如果使用rememberMe的功能可以访问
         *   perms: 该资源必须得到资源权限才可以访问
         *   role: 该资源必须得到角色权限才可以访问
         *
         */
        Map<String, String> filterMap = new LinkedHashMap<String, String>();

        filterMap.put("/user/add","perms[user:add]");
        filterMap.put("/user/update","perms[user:update]");
        filterMap.put("/user/*", "authc");


        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        //设置拦截后跳转的路径
        shiroFilterFactoryBean.setLoginUrl("/toLogin");
        shiroFilterFactoryBean.setUnauthorizedUrl("/unAuth");

        return shiroFilterFactoryBean;
    }


    /**
     * 创建DefaultWebSecurityManager
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm")UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(userRealm);
        //关联Realm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 创建Realm
     */
    @Bean(name = "userRealm")
    public UserRealm getRealm() {
        return new UserRealm();
    }
}
```

运行后，mecle用户只要添加的权限，jack用户只要修改的权限！！！
