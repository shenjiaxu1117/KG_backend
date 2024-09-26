package com.chen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.mapper.UserMapper;
import com.chen.pojo.User;
import com.chen.service.UserService;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

//    @Override
//    public List<User> getBySex(String sex) {
//        return List.of();
//    }
}
