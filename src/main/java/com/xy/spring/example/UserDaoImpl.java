package com.xy.spring.example;

import com.xy.spring.annotations.Repository;

/**
 * UserDaoImpl - 用户数据访问实现类
 * UserDaoImpl - User data access implementation class
 */
@Repository
public class UserDaoImpl implements UserDao {
    
    @Override
    public void save(String username) {
        System.out.println("UserDao: Saving user " + username + " to database...");
        // 模拟数据库保存
        // Simulate database save
    }
    
    @Override
    public String findById(int id) {
        System.out.println("UserDao: Finding user by id " + id + " from database...");
        // 模拟数据库查询
        // Simulate database query
        return "User_" + id;
    }
}
