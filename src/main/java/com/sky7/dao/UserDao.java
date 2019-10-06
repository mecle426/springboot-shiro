package com.sky7.dao;

import com.sky7.domain.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface UserDao {
//    通过用户名查询用户信息
    @Select({"select * from user where name = #{name}"})
    public User findByName(String name);
}
