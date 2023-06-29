package com.pcy;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
@Mapper
@Repository
public interface UserService {
    public String getShortUrl(String longUrl) throws IOException;

    public String getLongUrl(String shortUrl) throws IOException;
}
