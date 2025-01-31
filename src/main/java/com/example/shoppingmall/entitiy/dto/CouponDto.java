package com.example.shoppingmall.entitiy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto {

    private Long coupon_id;
    private String coupon_name;
    private String description;
    private int min_order;
    private LocalDateTime endDate;
    private boolean has_coupon;

}
