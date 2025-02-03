package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductRecommendationListener {

    private final ProductService productService;
    private final RedisTemplate<String, List<Long>> redisTemplate;
    private static final String REDIS_KEY_PREFIX = "user:recommendations:";
    private static final long CACHE_TTL_HOURS = 24; // 추천 데이터 24시간 유지

    @KafkaListener(topics = "product-views", groupId = "product-recommendation-group")
    public void listen(String message) {
        String[] parts = message.split(":");
        String userId = parts[0];
        Long productId = Long.parseLong(parts[1]);
        
        Product viewedProduct = productService.findById(productId);
        List<Product> recommendations = productService.findTopProductsByCategory(viewedProduct.getCategory(), 8);
        
        // Long 타입으로 명시적 변환
        List<Long> recommendations_id = recommendations.stream()
            .map(product -> Long.valueOf(product.getId()))  // 명시적으로 Long으로 변환
            .collect(Collectors.toList());
            
        String redisKey = REDIS_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(redisKey, recommendations_id, CACHE_TTL_HOURS, TimeUnit.HOURS);
    }


    public List<Long> getRecommendationsForUser(String userId) {

    String redisKey = REDIS_KEY_PREFIX + userId;
        try {
            Object result = redisTemplate.opsForValue().get(redisKey);
            if (result instanceof List<?>) {
                List<?> list = (List<?>) result;
                return list.stream()
                    .map(item -> {
                        if (item instanceof Number) {
                            return ((Number) item).longValue();
                        }
                        return Long.valueOf(String.valueOf(item));
                    })
                    .collect(Collectors.toList());
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error fetching recommendations: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    } 
} 