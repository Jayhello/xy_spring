package com.xy.example.service;

import com.xy.spring.annotation.Autowired;
import com.xy.spring.annotation.Component;

/**
 * 订单服务，依赖UserService
 */
@Component
public class OrderService {

    @Autowired
    private UserService userService;

    public String createOrder(String username, String product) {
        // 先验证用户
        String userInfo = userService.getUser(username);
        System.out.println("用户验证成功: " + userInfo);
        
        // 创建订单
        System.out.println("正在为用户 " + username + " 创建订单: " + product);
        return "订单创建成功！商品: " + product;
    }

    public String getOrder(String orderId) {
        System.out.println("正在查询订单: " + orderId);
        return "订单详情: " + orderId;
    }
}
