package com.erywim.order.controller;

import com.erywim.order.bean.Order;
import com.erywim.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope //设置自动刷新，当配置中心内容改变时，会自动更新
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Value("${order.timeout}")
    private String timeout;
    @Value("${order.auto-confirm}")
    private String autoConfirm;

    @GetMapping("config")
    public String getConfig() {
        return "timeout:" + timeout + " autoConfirm:" + autoConfirm;
    }

    @GetMapping("/createOrder")
    public Order createOrder(@RequestParam("id") Long id,
                             @RequestParam("userId")Long userId) {
        return orderService.createOrder(id, userId);
    }
}
