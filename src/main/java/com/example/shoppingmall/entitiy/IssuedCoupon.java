package com.example.shoppingmall.entitiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuedCoupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon; // 발급된 쿠폰 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 쿠폰을 발급받은 회원 정보

    @Column(nullable = false)
    private LocalDateTime issuedDate; // 쿠폰 발급 날짜

    @Column(nullable = false)
    private boolean isUsed = false; // 쿠폰 사용 여부

    private LocalDateTime usedDate; // 쿠폰 사용 날짜
}
