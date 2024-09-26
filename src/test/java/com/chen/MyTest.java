package com.chen;


import com.chen.pojo.User;
import com.chen.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyTest {

    @Autowired
    private UserService userService;

    @Test
    public void test() {
        User user = userService.getById(1689549121492402177L);

        System.out.println(user);


    }


}
