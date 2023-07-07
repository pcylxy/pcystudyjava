package com.example.urlconsumer.service;

import com.pcy.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import java.io.IOException;


@Service
public class OrderService {

    @DubboReference(version = "1.0")
    private UserService userService;

    public String getOrder(String longUrl,int timeout) throws IOException {
        return userService.getShortUrl(longUrl,timeout);
    }

    public String getOrder2(String shortUrl) throws IOException {
        return userService.getLongUrl(shortUrl);

    }
}
