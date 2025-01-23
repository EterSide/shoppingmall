package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/list")
    public String list(Model model) {
        return "coupon_list";
    }

    @GetMapping("/add")
    public String addCoupon() {
        return "add_coupon";
    }

    @PostMapping("/add")
    public String addCoupon(Coupon coupon, Model model) {
        return "add_coupon";
    }



}
