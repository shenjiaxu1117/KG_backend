package com.chen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);   // 这里的Application.class对应的启动类的类名
        System.out.println("Spring项目启动成功");
    }
    static {
        System.setProperty("druid.mysql.usePingMethod","false");
    }
}