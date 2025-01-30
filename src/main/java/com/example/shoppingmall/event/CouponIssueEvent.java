package com.example.shoppingmall.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor  // JSON 역직렬화를 위해 필요
public class CouponIssueEvent {
    private Long couponId;
    private Long memberId;
    private LocalDateTime issuedAt;
}