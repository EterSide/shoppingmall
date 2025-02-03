package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.entitiy.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductViewEventService {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "product-views";

    public void sendProductViewEvent(Long userId, Product product) {
        // userId와 productId를 구분자로 구분하여 전송
        String message = userId + ":" + product.getId();
        kafkaTemplate.send(TOPIC, message);
    }


} 