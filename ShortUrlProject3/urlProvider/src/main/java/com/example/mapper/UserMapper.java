package com.example.mapper;

import com.example.pojo.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    @Select("select long_url from user where short_url = #{shortUrl}")
    User selectAll(String shortUrl);

    @Insert("insert into user(short_url,long_url) values (#{shortUrl},#{longUrl})")
    void putOne(String shortUrl, String longUrl,long createdTime,long expiredTime);

    @Select("select short_url from user where long_url= #{longUrl}")
    User selectShortUrl(String longUrl);
    @Delete("delete from user where short_url=#{shortUrl}")
    void deleteOne(String shortUrl);
    @Delete("delete from user where expiredTime<#{currentTime}")
    void deleteExpired(long currentTime);
}
