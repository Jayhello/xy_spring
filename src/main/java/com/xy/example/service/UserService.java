package com.xy.example.service;

import com.xy.spring.annotation.Component;

/**
 * 用户服务
 */
@Component
public class UserService {

    public String createUser(String username) {
        System.out.println("正在创建用户: " + username);
        return "用户 " + username + " 创建成功！";
    }

    public String getUser(String username) {
        System.out.println("正在查询用户: " + username);
        return "用户信息: " + username;
    }

    public void deleteUser(String username) {
        System.out.println("正在删除用户: " + username);
    }
}
