package com.example.provider.service;

import com.pcy.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.RequestBody;

@DubboService(version ="2.0")
public class UserServiceImpl2 implements UserService {
    public String getUser( String longUrl){
        return "备用";
    }
}
