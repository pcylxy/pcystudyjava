package com.example.consumer.service;

import com.pcy.UserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
@DubboReference(version = "1.0")
private UserService userService;
public String getOrder( String longUrl){
    /*userService.getUser();*/

    return userService.getUser(longUrl);
}
}
