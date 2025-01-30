package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.entitiy.status.CouponStatus;
import com.example.shoppingmall.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssueService couponIssueService;
    private static final Logger log = LoggerFactory.getLogger(CouponService.class);

    public Coupon createCoupon(Coupon coupon) {
        Coupon savedCoupon = couponRepository.save(coupon);
        log.info("Initializing Redis quantity for coupon ID: {}, Quantity: {}", 
            savedCoupon.getId(), savedCoupon.getQuantity());
        couponIssueService.initializeCouponQuantity(savedCoupon.getId(), savedCoupon.getQuantity());
        return savedCoupon;
    }

    public List<Coupon> findAll() {
        return couponRepository.findAll();
    }

    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }


    public Optional<Coupon> findById(Long id) {
        return couponRepository.findById(id);
    }

    public Coupon update(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public void delete(Coupon coupon) {
        couponRepository.delete(coupon);
    }

    public List<Coupon> findByStatus(CouponStatus status) {
        return couponRepository.findByStatus(status);
    }
    public List<Coupon> findByStatusAndIsSpecial(CouponStatus status, Boolean isSpecial) {
        return couponRepository.findByStatusAndIsSpecial(status,isSpecial);
    }

    public List<Coupon> findByStatusAndIsSpecialAndStartDateBefore(CouponStatus status, Boolean isSpecial) {
        return couponRepository.findByStatusAndIsSpecialAndStartDateBefore(status,isSpecial, LocalDateTime.now());
    }


}
