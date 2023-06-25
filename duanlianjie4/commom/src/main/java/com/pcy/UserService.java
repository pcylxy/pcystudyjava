package com.pcy;

import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public interface UserService {
    public String getShortUrl(String longUrl) throws IOException;

    public String getLongUrl(String shortUrl) throws IOException;
}
