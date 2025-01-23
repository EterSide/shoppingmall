package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.service.CouponService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {

        List<Coupon> coupons = couponService.findAll();
        model.addAttribute("coupons", coupons);
        return "coupon_list";
    }

    @GetMapping("/register")
    public String addCoupon() {
        return "register_coupon";
    }

    @PostMapping("/register")
    public String addCoupon(Coupon coupon, Model model) {

        Coupon cp = new Coupon();
        cp.setName(coupon.getName());
        cp.setDescription(coupon.getDescription());
        cp.setDiscount(coupon.getDiscount());
        cp.setMinOrderAmount(coupon.getMinOrderAmount());
        cp.setMaxOrderAmount(coupon.getMaxOrderAmount());
        cp.setQuantity(coupon.getQuantity());
        cp.setStartDate(coupon.getStartDate());
        cp.setEndDate(coupon.getEndDate());

        couponService.save(cp);
        return "redirect:/coupon/list";
    }



}
