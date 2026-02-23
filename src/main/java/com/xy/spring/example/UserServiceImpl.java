package com.xy.spring.example;

import com.xy.spring.annotations.Autowired;
import com.xy.spring.annotations.Service;

/**
 * UserServiceImpl - 用户业务逻辑实现类
 * UserServiceImpl - User business logic implementation class
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserDao userDao;
    
    @Override
    public void registerUser(String username) {
        System.out.println("UserService: Registering user " + username);
        // 业务逻辑处理
        // Business logic processing
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        userDao.save(username);
        System.out.println("UserService: User " + username + " registered successfully");
    }
    
    @Override
    public String getUserInfo(int userId) {
        System.out.println("UserService: Getting user info for id " + userId);
        String user = userDao.findById(userId);
        return "User information: " + user;
    }
}
