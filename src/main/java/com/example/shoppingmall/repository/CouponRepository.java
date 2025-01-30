package com.example.shoppingmall.repository;

import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.entitiy.status.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByStatus(CouponStatus status);
    List<Coupon> findByStatusAndIsSpecial(CouponStatus status,boolean isSpecial);

    List<Coupon> findByStatusAndIsSpecialAndStartDateBefore(CouponStatus status, boolean isSpecial, LocalDateTime beforeStOrStartDate);


    //List<Coupon> findByMemberIsNull();

    //List<Coupon> findByMemberId(Long id);

    //Long member(Member member);
}
