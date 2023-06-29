package com.example.urlconsumer.controller;

import com.example.urlconsumer.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class OrderController {
    @Resource
    private OrderService orderService;

    @GetMapping("/longUrl")
    public String getShortUrl(@RequestParam String longUrl) throws IOException {
        return orderService.getOrder(longUrl);
    }

    @RequestMapping ("/shortUrl")
    @ResponseStatus(HttpStatus.FOUND)
    public String getLongUrl(@RequestParam String shortUrl) throws IOException {
        String newPage=orderService.getOrder2(shortUrl);

        return "redirect:/"+newPage;
    }
}
