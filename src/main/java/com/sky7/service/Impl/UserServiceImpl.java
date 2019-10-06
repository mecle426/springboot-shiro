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
