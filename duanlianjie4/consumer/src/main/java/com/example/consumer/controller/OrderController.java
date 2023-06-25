package com.example.consumer.controller;

import com.example.consumer.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;


public class OrderController {
    @Resource
    private OrderService orderService;

    @GetMapping("/longUrl")
    public String getShortUrl(@RequestBody String longUrl) throws IOException {
        return orderService.getOrder(longUrl);
    }

    @GetMapping("/shortUrl")
    public String getLongUrl(@RequestBody String shortUrl) {
        return orderService.getOrder2(shortUrl);
    }
}
