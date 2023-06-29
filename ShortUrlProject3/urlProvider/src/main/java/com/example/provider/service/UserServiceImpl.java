package com.example.provider.service;

import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.staticmethod.sqlsessionFactory;
import com.pcy.UserService;
import org.apache.curator.shaded.com.google.common.hash.BloomFilter;
import org.apache.curator.shaded.com.google.common.hash.Funnels;
import org.apache.curator.shaded.com.google.common.hash.Hashing;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@DubboService(version = "1.0")
public class UserServiceImpl implements UserService {
    private static final int size=1000000;//???
    private static final double fpp=0.00001;
    private static BloomFilter<String> bloomFilter=BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()),size,fpp);
    private static final String BASE_62_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = BASE_62_CHAR.length();
    @Autowired
    RedisTemplate redisTemplate;
    BoundHashOperations car = redisTemplate.boundHashOps("car");
    public synchronized String getShortUrl(String longUrl) throws IOException {
        if (StringUtils.isEmpty(longUrl)) {
            throw new RuntimeException("longUrl is empty");
        }
        if (bloomFilter.mightContain(longUrl)){

            if (car.hasKey(longUrl)) {
                return car.get(longUrl).toString();
            }else {
                    SqlSessionFactory sqlSessionFactory = sqlsessionFactory.getsqlsessionFactory();
                    SqlSession sqlSession = sqlSessionFactory.openSession();
                    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
                    User user = userMapper.selectShortUrl(longUrl);
                    sqlSession.commit();
                    sqlSession.close();
                    car.putIfAbsent(longUrl, user.getLong_url());
                    return user.getLong_url();
            }
        }
        String[] split = longUrl.split("/");
        StringBuilder pre = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            pre.append(split[i]).append("/");
        }
        String temp = pre.toString();
        SqlSessionFactory sqlSessionFactory = sqlsessionFactory.getsqlsessionFactory();
/*            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);*/
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.selectAll(pre.toString());
        pre.append(Base10toBase62(Hashing.murmur3_32().hashString(split[split.length - 1], StandardCharsets.UTF_8).padToLong()));
        try {
            userMapper.putOne(pre.toString(), longUrl);
        } catch (Exception e) {
            String timeStamp = System.currentTimeMillis() + "";
            pre = new StringBuilder(temp);
            pre.append(Base10toBase62(Hashing.murmur3_32().hashString(split[split.length - 1] + timeStamp, StandardCharsets.UTF_8).padToLong()));
            userMapper.putOne(pre.toString(), longUrl);
        }
        sqlSession.commit();
        sqlSession.close();
        bloomFilter.put(longUrl);
        return pre.toString();
    }

    @Transactional
    @Commit
    public String getLongUrl(String shortUrl) throws IOException {
        /*BoundHashOperations car = redisTemplate.boundHashOps("car");*/
        if (car.hasKey(shortUrl)) {
            return car.get(shortUrl).toString();
        }
        try {
            SqlSessionFactory sqlSessionFactory = sqlsessionFactory.getsqlsessionFactory();
/*            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);*/
            SqlSession sqlSession = sqlSessionFactory.openSession();
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.selectAll(shortUrl);
            sqlSession.commit();
            sqlSession.close();
            car.putIfAbsent(shortUrl, user.getLong_url());
            return user.getLong_url();
        } catch (Exception e) {
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
