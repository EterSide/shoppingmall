package com.example.shoppingmall.repository;

import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.entitiy.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByMemberIsNull();

}
