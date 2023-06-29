package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
    @Bean
public RedisConnectionFactory redisConnectionFactory(){
        //单机
        RedisStandaloneConfiguration standaloneConfiguration=new RedisStandaloneConfiguration("127.0.0.1");
  //集群new RedisCluster...
        //连接池
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        jedisPoolConfig.setMaxWaitMillis(0);
        JedisClientConfiguration clientConfiguration=JedisClientConfiguration.builder().usePooling().poolConfig(jedisPoolConfig).build();
    return new JedisConnectionFactory(standaloneConfiguration,clientConfiguration);
}
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());//设置序列化
        return redisTemplate;
    }

@Bean
public RedisTemplate tranRedisTemplate(RedisConnectionFactory redisConnectionFactory){
    RedisTemplate redisTemplate = new RedisTemplate();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    redisTemplate.setDefaultSerializer(new StringRedisSerializer());//设置序列化
    redisTemplate.setEnableTransactionSupport(true);
return redisTemplate;
    }
}
