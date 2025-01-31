package com.example.shoppingmall.controlloer;


import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.entitiy.IssuedCoupon;
import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.entitiy.dto.CouponDto;
import com.example.shoppingmall.entitiy.status.CouponStatus;
import com.example.shoppingmall.service.CouponIssueService;
import com.example.shoppingmall.service.CouponService;
import com.example.shoppingmall.service.IssuedCouponService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/issued_coupon")
public class IssuedCouponController {

    private final IssuedCouponService issuedCouponService;
    private final CouponService couponService;
    private final CouponIssueService couponIssueService;

    @GetMapping("/register")
    public String register(Model model, HttpSession session) {

        List<CouponDto> couponDtos = new ArrayList<>();

        Member member = (Member) session.getAttribute("member");
//        List<Coupon> coupons = couponService.findByStatus(CouponStatus.ACTIVE);
        List<Coupon> coupons = couponService.findByStatusAndIsSpecialAndStartDateBefore(CouponStatus.ACTIVE, false);

        if (member != null) {
            List<IssuedCoupon> issuedCoupons = issuedCouponService.findByMemberId(member.getId());
            model.addAttribute("issuedCoupons", issuedCoupons);

            for (Coupon coupon : coupons) {
                boolean hasCoupon = false;
                for(IssuedCoupon issuedCoupon : issuedCoupons) {
                    if(coupon.getId().equals(issuedCoupon.getCoupon().getId())) {
                        hasCoupon = true;
                    }
                }
                    couponDtos.add(new CouponDto(coupon.getId(), coupon.getName(), coupon.getDescription(), coupon.getMinOrderAmount(), coupon.getEndDate(), hasCoupon));
            }

        }
        model.addAttribute("coupons", couponDtos);
        return "register_issued_coupon";
    }

    @GetMapping("/register/special/")
    public String registerSpecial(Model model, HttpSession session) {
        List<CouponDto> couponDtos = new ArrayList<>();
        Member member = (Member) session.getAttribute("member");
        List<Coupon> coupons = couponService.findByStatusAndIsSpecialAndStartDateBefore(CouponStatus.ACTIVE,true);

        if (member != null) {
            List<IssuedCoupon> issuedCoupons = issuedCouponService.findByMemberId(member.getId());
            model.addAttribute("issuedCoupons", issuedCoupons);

            for (Coupon coupon : coupons) {
                boolean hasCoupon = false;
                for(IssuedCoupon issuedCoupon : issuedCoupons) {
                    if(coupon.getId().equals(issuedCoupon.getCoupon().getId())) {
                        hasCoupon = true;
                    }
                }
                couponDtos.add(new CouponDto(coupon.getId(), coupon.getName(), coupon.getDescription(), coupon.getMinOrderAmount(), coupon.getEndDate(), hasCoupon));
            }
        }
        model.addAttribute("coupons", couponDtos);
        return "register_special_issued_coupon";
    }

    @GetMapping("/register/{coupon_id}")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> register(@PathVariable Long coupon_id, HttpSession session) {

        Optional<Coupon> coupon = couponService.findById(coupon_id);
        Member member = (Member) session.getAttribute("member");
        IssuedCoupon issuedCoupon = new IssuedCoupon();

        Map<String, Boolean> response = new HashMap<>();

        boolean isTrue = true;

        if(coupon.isPresent() && issuedCouponService.findMemberAndCoupon(member.getId(), coupon_id) == null) {

            issuedCoupon.setCoupon(coupon.get());
            issuedCoupon.setMember(member);
            issuedCoupon.setIssuedDate(LocalDateTime.now());
            issuedCouponService.save(issuedCoupon);
            isTrue = false;

        }

        response.put("exists", isTrue);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {

        Member member = (Member) session.getAttribute("member");


        List<IssuedCoupon> issuedCoupons = issuedCouponService.findByMemberAndIsUsed(member.getId(), false);
        model.addAttribute("issuedCoupons", issuedCoupons);

        return "issued_coupon_list";
    }

    @PostMapping("/register/special/{coupon_id}")
    @ResponseBody
    public ResponseEntity<String> issueCoupon(
            @PathVariable Long coupon_id,
            HttpSession session) {

        // 세션에서 memberId 가져오기
        Member member = (Member) session.getAttribute("member");
        Long memberId = member.getId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요한 서비스입니다.");
        }

        boolean issued = couponIssueService.issueCoupon(coupon_id, memberId);

        if (issued) {
            return ResponseEntity.ok("쿠폰이 성공적으로 발급되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("쿠폰 수량이 소진되었습니다.");
        }
    }



}