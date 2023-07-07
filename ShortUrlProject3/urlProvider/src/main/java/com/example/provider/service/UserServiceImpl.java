package com.example.provider.service;

import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.staticmethod.sqlsessionFactory;
import com.pcy.UserService;
import org.apache.curator.shaded.com.google.common.hash.Hashing;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@DubboService(version = "1.0")
public class UserServiceImpl implements UserService {
    private static final int defaultExpiredTIme = 24;//
    private static final int size = 1000000;//
    private static final double fpp = 0.00001;//
    private static final String BASE_62_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = BASE_62_CHAR.length();
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private  UserMapper userMapper;
    Config config = new Config();
    RedissonClient redissonClient = Redisson.create(config);
    RBloomFilter<String> rBloomFilter = redissonClient.getBloomFilter("shortUrlList");

    public synchronized String getShortUrl(String longUrl, int timeout) {
        if (StringUtils.isEmpty(longUrl)) {
            throw new RuntimeException("longUrl is empty");
        }
        rBloomFilter.tryInit(size, fpp);
        if (rBloomFilter.contains(longUrl)) {
            String[] split = longUrl.split("/");
            StringBuilder pre = new StringBuilder();
            for (int i = 0; i < split.length - 1; i++) {
                pre.append(split[i]).append("/");
            }
            pre.append(Base10toBase62(Hashing.murmur3_32().hashString(split[split.length - 1], StandardCharsets.UTF_8).padToLong()));
            if (redisTemplate.hasKey(pre)) {
/*                long expireTime = redisTemplate.getExpire(pre);
                redisTemplate.boundValueOps(pre).expire(createRandom() * expireTime,
                        TimeUnit.MILLISECONDS);*/
                return pre.toString();
            } else {
                try {
                    User user = userMapper.selectShortUrl(longUrl);
                    redisTemplate.boundValueOps(user.getShort_url()).set(longUrl,
                            (long) createRandom() * timeout * 60 * 60 * 1000,
                            TimeUnit.MILLISECONDS);
                    return pre.toString();
                } catch (Exception e) {

                }
            }
        }
        String[] split = longUrl.split("/");
        StringBuilder pre = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            pre.append(split[i]).append("/");
        }
        String temp = pre.toString();
        User user = userMapper.selectAll(pre.toString());
        pre.append(Base10toBase62(Hashing.murmur3_32().hashString(split[split.length - 1], StandardCharsets.UTF_8).padToLong()));
        try {
            userMapper.putOne(pre.toString(), longUrl, System.currentTimeMillis(), System.currentTimeMillis() + timeout * 60 * 60 * 1000);
        } catch (Exception e) {
            String timeStamp = System.currentTimeMillis() + "";
            pre = new StringBuilder(temp);
            pre.append(Base10toBase62(Hashing.murmur3_32().hashString(split[split.length - 1] + timeStamp, StandardCharsets.UTF_8).padToLong()));
            userMapper.putOne(pre.toString(), longUrl, System.currentTimeMillis(), System.currentTimeMillis() + timeout * 60 * 60 * 1000);
        }
        rBloomFilter.add(longUrl);
        return pre.toString();
    }
    @Commit
    public String getLongUrl(String shortUrl) throws IOException {
        BoundValueOperations name = redisTemplate.boundValueOps(shortUrl);
        if (redisTemplate.hasKey(shortUrl)) {
            /*long expireTime = redisTemplate.getExpire(shortUrl);
            redisTemplate.boundValueOps(shortUrl).expire(createRandom() * expireTime,
                    TimeUnit.MILLISECONDS);*/
            return redisTemplate.boundValueOps(shortUrl).get().toString();
        }
        try {
            User user = userMapper.selectAll(shortUrl);
            if (System.currentTimeMillis()>=user.getExpiredTime()){
                userMapper.deleteOne(shortUrl);
                throw new RuntimeException("shortUrl is invalid!");
            }
            redisTemplate.boundValueOps(shortUrl).set(user.getLong_url(),
                    (long) createRandom() * (user.getExpiredTime() - System.currentTimeMillis()),
                    TimeUnit.MILLISECONDS);
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

    private double createRandom() {
        return 0.2 + Math.random() * 3 / 10;
    }
}
