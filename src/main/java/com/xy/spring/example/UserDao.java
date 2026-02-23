package com.xy.spring.example;

/**
 * UserDao接口 - 数据访问层接口
 * UserDao interface - Data access layer interface
 */
public interface UserDao {
    void save(String username);
    String findById(int id);
}
