package com.xy.spring.example;

/**
 * UserService接口 - 业务逻辑层接口
 * UserService interface - Business logic layer interface
 */
public interface UserService {
    void registerUser(String username);
    String getUserInfo(int userId);
}
