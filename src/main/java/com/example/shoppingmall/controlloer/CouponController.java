package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.service.CouponService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;
    private static final Logger log = LoggerFactory.getLogger(CouponController.class);

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

    @GetMapping("/special/register")
    public String addSpecialCoupon() {
        return "register_special_coupon";
    }

    @PostMapping("/register")
    public String addCoupon(Coupon coupon, Model model) {
        couponService.save(coupon);
        return "redirect:/coupon/list";
    }

    @PostMapping("/special/register")
    public String addRedisCoupon(Coupon coupon) {

        log.info("Creating coupon with quantity: {}", coupon.getQuantity());
        Coupon createdCoupon = couponService.createCoupon(coupon);
        log.info("Coupon created with ID: {}", createdCoupon.getId());

        return "redirect:/coupon/list";
    }

}
