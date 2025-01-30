package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.entitiy.IssuedCoupon;
import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.repository.CouponRepository;
import com.example.shoppingmall.repository.MemberRepository;
import com.example.shoppingmall.event.CouponIssueEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponIssueConsumer {

    private final IssuedCouponService issuedCouponService;
    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;
    private static final Logger log = LoggerFactory.getLogger(CouponIssueConsumer.class);

    @KafkaListener(
        topics = "coupon-issue", 
        groupId = "coupon-issue-group",
        containerFactory = "couponKafkaListenerContainerFactory"
    )
    public void consumeCouponIssue(CouponIssueEvent event) {
        log.info("Received coupon issue event: {}", event);
        
        try {
            Coupon coupon = couponRepository.findById(event.getCouponId())
                    .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));

            Member member = memberRepository.findById(event.getMemberId())
                    .orElseThrow(() -> new EntityNotFoundException("Member not found"));

            // 중복 발급 체크
            IssuedCoupon existingCoupon = issuedCouponService.findMemberAndCoupon(
                member.getId(), coupon.getId());
            
            if (existingCoupon != null) {
                log.warn("Coupon already issued to member. MemberId: {}, CouponId: {}", 
                    member.getId(), coupon.getId());
                return;
            }

            IssuedCoupon issuedCoupon = new IssuedCoupon();
            issuedCoupon.setCoupon(coupon);
            issuedCoupon.setMember(member);
            issuedCoupon.setIssuedDate(event.getIssuedAt());
            issuedCoupon.setUsed(false);
            coupon.setQuantity(coupon.getQuantity() - 1);
            couponRepository.save(coupon);

            issuedCouponService.save(issuedCoupon);
            log.info("Successfully saved issued coupon. MemberId: {}, CouponId: {}", 
                member.getId(), coupon.getId());
            
        } catch (Exception e) {
            log.error("Error while processing coupon issue event", e);
        }
    }
}