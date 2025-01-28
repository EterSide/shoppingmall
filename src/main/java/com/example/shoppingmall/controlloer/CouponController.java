package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.service.CouponService;
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
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {

        List<Coupon> coupons = couponService.findAll();
        model.addAttribute("coupons", coupons);
        return "coupon_list";
    }

    @GetMapping("/update/{coupon_id}")
    public String update(Model model, @PathVariable long coupon_id) {

        Optional<Coupon> couponId = couponService.findById(coupon_id);
        Coupon coupon = new Coupon();

        if (couponId.isPresent()) {
            coupon = couponId.get();
        }

        model.addAttribute("coupon", coupon);

        return "update_coupon";
    }

    @PostMapping("/update/{coupon_id}")
    public String update(Coupon coupon, @PathVariable long coupon_id) {

        Optional<Coupon> couponId = couponService.findById(coupon_id);
        if (couponId.isPresent()) {
            Coupon updateCoupon = new Coupon();
            updateCoupon = couponId.get();
            updateCoupon.setName(coupon.getName());
            updateCoupon.setDescription(coupon.getDescription());
            updateCoupon.setDiscount(coupon.getDiscount());
            updateCoupon.setQuantity(coupon.getQuantity());
            updateCoupon.setMaxOrderAmount(coupon.getMaxOrderAmount());
            updateCoupon.setMinOrderAmount(coupon.getMinOrderAmount());
            updateCoupon.setStartDate(coupon.getStartDate());
            updateCoupon.setEndDate(coupon.getEndDate());
            couponService.update(updateCoupon);

        }

        return "redirect:/coupon/list";
    }

    @GetMapping("/delete/{coupon_id}")
    public String delete(@PathVariable long coupon_id) {

        Optional<Coupon> couponId = couponService.findById(coupon_id);

        if (couponId.isPresent()) {
            Coupon coupon = couponId.get();
            couponService.delete(coupon);
        }

        return "redirect:/coupon/list";
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
