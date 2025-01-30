package com.example.shoppingmall.service;

import com.example.shoppingmall.repository.IssuedCouponRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.example.shoppingmall.event.CouponIssueEvent;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponIssueService {

    private static final Logger log = LoggerFactory.getLogger(CouponIssueService.class);

    private final RedisTemplate<String, String> redisCouponTemplate;
    private final KafkaTemplate<String, Object> couponKafkaTemplate;
    private final IssuedCouponRepository issuedCouponRepository;

    private static final String COUPON_QUANTITY_PREFIX = "coupon:quantity:";
    private static final String COUPON_ISSUE_TOPIC = "coupon-issue";
    private static final String COUPON_ISSUED_USER_PREFIX = "coupon:issued:";  // 추가


    public boolean issueCoupon(Long couponId, Long memberId) {
        String quantityKey = COUPON_QUANTITY_PREFIX + couponId;
        String issuedKey = COUPON_ISSUED_USER_PREFIX + couponId + ":" + memberId;
        log.info("Attempting to issue coupon. Key: {}", quantityKey);

        //redis 해당 사용자가 발급한 내역이 있는지 확인 false
        Boolean isAlreadyIssued = redisCouponTemplate.hasKey(issuedKey);
        if (Boolean.TRUE.equals(isAlreadyIssued)) {
            log.warn("User {} already has coupon {}", memberId, couponId);
            return false;
        }

        // 현재 수량 확인
        String currentQuantity = redisCouponTemplate.opsForValue().get(quantityKey);
        log.info("Current quantity in Redis: {}", currentQuantity);

        if (currentQuantity == null) {
            log.error("No quantity found in Redis for key: {}", quantityKey);
            return false;
        }

        // Redis의 DECR 명령어를 사용하여 원자적으로 수량 감소
        Long remainingQuantity = redisCouponTemplate.opsForValue()
                .decrement(quantityKey);
        log.info("Remaining quantity after decrement: {}", remainingQuantity);

        if (remainingQuantity == null || remainingQuantity < 0) {
            log.warn("Invalid remaining quantity: {}", remainingQuantity);
            // 수량이 부족한 경우, 다시 증가시키고 실패 반환
            if (remainingQuantity != null) {
                redisCouponTemplate.opsForValue().increment(quantityKey);
            }
            return false;
        }

        //redis에 쿠폰 발급 내역 저장 (userId,쿠폰id)
        redisCouponTemplate.opsForValue().set(issuedKey, LocalDateTime.now().toString());
        log.info("Saved coupon issue record in Redis for user {} and coupon {}", memberId, couponId);


        // Kafka로 쿠폰 발급 이벤트 발행
        CouponIssueEvent event = new CouponIssueEvent(couponId, memberId, LocalDateTime.now());
        couponKafkaTemplate.send(COUPON_ISSUE_TOPIC, event);
        log.info("Coupon issue event sent to Kafka for couponId: {}, memberId: {}", couponId, memberId);

        return true;
    }

    // Redis에 초기 쿠폰 수량 설정
    public void initializeCouponQuantity(Long couponId, int quantity) {
        String quantityKey = COUPON_QUANTITY_PREFIX + couponId;
        log.info("=== Starting Redis initialization ===");
        log.info("Key: {}, Quantity: {}", quantityKey, quantity);
        
        try {
            Boolean setResult = redisCouponTemplate.opsForValue()
                .setIfAbsent(quantityKey, String.valueOf(quantity));
            log.info("Redis set operation result: {}", setResult);
            
            // 설정된 값 확인
            String setQuantity = redisCouponTemplate.opsForValue().get(quantityKey);
            log.info("Verified quantity in Redis - Key: {}, Value: {}", quantityKey, setQuantity);
            
            if (setQuantity == null) {
                log.error("Failed to set value in Redis");
            }
        } catch (Exception e) {
            log.error("Error while setting Redis value", e);
        }
    }
}

