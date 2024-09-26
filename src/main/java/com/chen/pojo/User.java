package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@TableName("user_table")  // 指定实体类对应的表名,如果表名与实体类名一致则可省略
@Data
public class User {

    private Long id;


    //姓名
    private String name;


    //手机号
    private String phone;


    //性别 0 女 1 男
    private String sex;


    //身份证号
    private String idNumber;


    //头像
    private String avatar;


    //状态 0:禁用，1:正常
    private Integer status;
}