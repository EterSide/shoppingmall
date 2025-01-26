package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.entitiy.IssuedCoupon;
import com.example.shoppingmall.repository.CouponRepository;
import com.example.shoppingmall.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssuedCouponService {

    private final IssuedCouponRepository issuedCouponRepository;

    public List<IssuedCoupon> findAll() {
        return issuedCouponRepository.findAll();
    }

    public IssuedCoupon save(IssuedCoupon issuedCoupon) {
        return issuedCouponRepository.save(issuedCoupon);
    }


    public List<IssuedCoupon> findByMemberId(Long id) {
        return issuedCouponRepository.findByMemberId(id);
    }

    public List<IssuedCoupon> findMemberAndCoupon(Long member_id, Long coupon_id) {
        return issuedCouponRepository.findByMemberIdAndCouponId(member_id, coupon_id);
    }

}