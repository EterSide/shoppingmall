package com.example.shoppingmall.entitiy;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "cart", timeToLive = 60)
public class Cart extends BaseTimeEntity{

    @Id
    private String id;
    private String productId;

}

// (user_id_cart, [product_id,product_id,product_id...])