package com.example.shoppingmall.repository;

import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.entitiy.IssuedCoupon;
import com.example.shoppingmall.entitiy.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {


    List<IssuedCoupon> findByMemberId(Long id);

    IssuedCoupon findByMemberIdAndCouponId(Long id, Long couponId);

    List<IssuedCoupon> findByMemberIdAndIsUsed(Long id, Boolean isUsed);

    //Long member(Member member);
}
