package com.example.shoppingmall.controlloer;


import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.entitiy.IssuedCoupon;
import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.service.CouponService;
import com.example.shoppingmall.service.IssuedCouponService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/issued_coupon")
public class IssuedCouponController {

    private final IssuedCouponService issuedCouponService;
    private final CouponService couponService;

    @GetMapping("/register")
    public String register(Model model, HttpSession session) {

        Member member = (Member) session.getAttribute("member");

        if(member != null) {
            List<IssuedCoupon> issuedCoupons = issuedCouponService.findByMemberId(member.getId());
            model.addAttribute("issuedCoupons", issuedCoupons);
        }



        List<Coupon> coupons = couponService.findAll();
        model.addAttribute("coupons", coupons);

        return "register_issued_coupon";
    }

    @GetMapping("/register/{coupon_id}")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> register(Model model, @PathVariable Long coupon_id, HttpSession session) {

        Optional<Coupon> coupon = couponService.findById(coupon_id);
        Member member = (Member) session.getAttribute("member");
        IssuedCoupon issuedCoupon = new IssuedCoupon();

        Map<String, Boolean> response = new HashMap<>();

        boolean isTrue = false;

        if(coupon.isPresent()) {

            issuedCoupon.setCoupon(coupon.get());
            issuedCoupon.setMember(member);
            issuedCoupon.setIssuedDate(LocalDateTime.now());
            issuedCouponService.save(issuedCoupon);
            isTrue = true;

        }

        response.put("isTrue", isTrue);

        return ResponseEntity.ok(response);

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
