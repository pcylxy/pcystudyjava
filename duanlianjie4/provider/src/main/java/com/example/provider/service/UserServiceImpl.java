package com.example.provider.service;

import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.pcy.UserService;
import org.apache.curator.shaded.com.google.common.hash.Hashing;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@DubboService(version = "1.0")
public class UserServiceImpl implements UserService {
    private final static ConcurrentHashMap<String, String> shortMap = new ConcurrentHashMap<>();
    private static final String BASE_62_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = BASE_62_CHAR.length();

    public String getShortUrl(String longUrl) throws IOException {
        if (StringUtils.isEmpty(longUrl)) {
            throw new RuntimeException("longUrl is empty");
            /*return "400";*/
        }
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        String[] split = longUrl.split("/");
        StringBuilder pre = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            pre.append(split[i]).append("/");
        }String temp=pre.toString();
        pre.append(Base10toBase62(Hashing.murmur3_32().hashString(split[split.length - 1], StandardCharsets.UTF_8).padToLong()));
        /*shortMap.put(pre.toString(), longUrl);*/
        User user = userMapper.selectAll(pre.toString());
        if (user==null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeStamp = dateFormat.format(new Date());
            pre=new StringBuilder(temp);
            pre.append(Base10toBase62(Hashing.murmur3_32().hashString(split[split.length - 1]+timeStamp, StandardCharsets.UTF_8).padToLong()));
        }
        userMapper.putOne(pre.toString(),longUrl);
        sqlSession.commit();
        sqlSession.close();
        return pre.toString();
    }

    public String getLongUrl(String shortUrl) throws IOException {
        if (shortMap.containsKey(shortUrl)) {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            SqlSession sqlSession = sqlSessionFactory.openSession(true);
            UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
            User user = userMapper.selectAll(shortUrl);
            sqlSession.commit();
            sqlSession.close();
            return user.getLong_url();
        } else {
            throw new RuntimeException("shortUrl is not found!");
        }
    }

    public String Base10toBase62(long i) {
        StringBuilder sb = new StringBuilder("");
        if (i == 0) {
            return "a";
        }
        while (i > 0) {
            i = Base10toBase62(i, sb);
        }
        return sb.reverse().toString();
    }

    private long Base10toBase62(long i, final StringBuilder sb) {
        int rem = (int) (i % BASE);
        sb.append(BASE_62_CHAR.charAt(rem));
        return i / BASE;
    }
}
