package com.example.shoppingmall.controlloer;


import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.entitiy.IssuedCoupon;
import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.service.CouponService;
import com.example.shoppingmall.service.IssuedCouponService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/issued_coupon")
public class IssuedCouponController {

    private final IssuedCouponService issuedCouponService;
    private final CouponService couponService;

    @GetMapping("/register")
    public String register(Model model, HttpSession session) {

        Member member = (Member) session.getAttribute("user");

        if(member != null) {
            List<IssuedCoupon> issuedCoupons = issuedCouponService.findByMemberId(member.getId());
            model.addAttribute("issuedCoupons", issuedCoupons);
        }



        List<Coupon> coupons = couponService.findAll();
        model.addAttribute("coupons", coupons);

        return "register_issued_coupon";
    }

    @PostMapping("/register/{coupon_id}")
    public String register(Model model, @PathVariable Long coupon_id, HttpSession session) {

        Optional<Coupon> coupon = couponService.findById(coupon_id);
        Member member = (Member) session.getAttribute("user");
        IssuedCoupon issuedCoupon = new IssuedCoupon();

        if(coupon.isPresent()) {

            issuedCoupon.setCoupon(coupon.get());
            issuedCoupon.setMember(member);
            issuedCouponService.save(issuedCoupon);

        }

        return "";

    }

//    @GetMapping("/list")
//    public String list(Model model, HttpSession session) {
//
//        List<IssuedCoupon> coupons = issuedCouponService.findAll();
//        model.addAttribute("coupons", coupons);
//        return "coupon_list";
//    }
//
//    @GetMapping("/register")
//    public String addCoupon() {
//        return "register_coupon";
//    }

//    @PostMapping("/register")
//    public String addCoupon(IssuedCoupon coupon, Model model) {
//
//        Coupon cp = new Coupon();
//        cp.setName(coupon.getName());
//        cp.setDescription(coupon.getDescription());
//        cp.setDiscount(coupon.getDiscount());
//        cp.setMinOrderAmount(coupon.getMinOrderAmount());
//        cp.setMaxOrderAmount(coupon.getMaxOrderAmount());
//        cp.setQuantity(coupon.getQuantity());
//        cp.setStartDate(coupon.getStartDate());
//        cp.setEndDate(coupon.getEndDate());
//
//        issuedCouponService.save(cp);
//        return "redirect:/coupon/list";
//    }



}
