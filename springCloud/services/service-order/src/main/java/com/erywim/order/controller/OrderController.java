package com.erywim.order.controller;

import com.erywim.order.bean.Order;
import com.erywim.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/createOrder")
    public Order createOrder(@RequestParam("id") Long id,
                             @RequestParam("userId")Long userId) {
        return orderService.createOrder(id, userId);
    }
}
