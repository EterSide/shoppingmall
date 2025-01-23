package com.example.shoppingmall.entitiy;

import com.example.shoppingmall.entitiy.status.CouponStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description; // 설명

    @Column(nullable = false)
    private int discount; // 할인율

    @Column(nullable = false)
    private LocalDateTime startDate; 
    // 쿠폰 유효 기간
    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private int minOrderAmount; // 쿠폰 적용을 하기위한 최소 금액 조건

    @Column(nullable = false)
    private int maxOrderAmount; // 쿠폰 최대 할인 가격

    @Column(nullable = false)
    private int quantity; // 쿠폰 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private CouponStatus status = CouponStatus.ACTIVE;

}
