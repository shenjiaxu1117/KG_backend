package com.chen;


import com.chen.mapper.EntityMapper;
import com.chen.mapper.UserMapper;
import com.chen.pojo.Entity;
import com.chen.pojo.User;
import com.chen.service.EntityService;
import com.chen.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyTest {

    @Autowired
    private UserService userService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private EntityMapper entityMapper;

    @Test
    public void test() {
        User user = userService.getById(1689549121492402177L);

        System.out.println(user);
    }


    @Test
    public void test1() {
        List<Entity> list = entityMapper.getByGraphId(2);
        System.out.println(list);

    }


}
