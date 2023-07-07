package com.example.urlconsumer.controller;

import com.example.urlconsumer.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RestController
public class OrderController {
    @Resource
    private OrderService orderService;

    @GetMapping("/longUrl")
    public String getShortUrl(@RequestParam String longUrl,@RequestParam(required = false) int timeout) throws IOException {
        return orderService.getOrder(longUrl,timeout);
    }

    @RequestMapping (value = "/shortUrl",method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.FOUND)
    public ModelAndView getLongUrl(@RequestParam String shortUrl) throws IOException {
        String newPage=orderService.getOrder2(shortUrl);
        ModelAndView modelAndView=new ModelAndView(newPage);
        return modelAndView;
    }
}
