package com.example.consumer.controller;

import com.example.consumer.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class OrderController {
    @Resource
    private OrderService orderService;
    @GetMapping("/order")
    public  String getUser(@RequestBody String longUrl){
        return orderService.getOrder(longUrl);
    }
}
