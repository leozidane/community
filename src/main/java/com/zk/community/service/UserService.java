package com.zk.community.service;

import com.zk.community.dao.UserMapper;
import com.zk.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int userId) {
        return userMapper.selectById(userId);
    }
}
