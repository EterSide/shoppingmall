package com.example.shoppingmall.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.shoppingmall.entitiy.Product;
import java.util.List;

@Configuration
public class RedisConfig {
    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    public RedisTemplate<String, List<Long>> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, List<Long>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(List.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, String> redisCouponTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        
        // Redis 연결 테스트
        template.afterPropertiesSet();
        try {
            template.getConnectionFactory().getConnection().ping();
            log.info("Successfully connected to Redis");
        } catch (Exception e) {
            log.error("Failed to connect to Redis", e);
        }
        
        return template;
    }
} 