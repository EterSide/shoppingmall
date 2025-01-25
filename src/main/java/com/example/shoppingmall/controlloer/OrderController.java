package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.*;
import com.example.shoppingmall.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;
    private final MemberService memberService;
    private final ProductImageService productImageService;
    private final CouponService couponService;
    private final IssuedCouponService issuedCouponService;
    private final OrderItemService orderItemService;
    private final DeliveryService deliveryService;

    @GetMapping("/{product_id}")
    public String orderProduct(@PathVariable Long product_id, Model model, HttpSession session) {

        Product product = productService.findById(product_id);
        List<ProductImage> images = productImageService.findByProductId(product.getId());

        Member member = (Member) session.getAttribute("member");

        if(member != null) {

            List<IssuedCoupon> issuedCoupons = issuedCouponService.findByMemberId(member.getId());

            model.addAttribute("coupons", issuedCoupons);

        }

//        if(member != null) {
//            //List<IssuedCoupon> coupons = issuedCouponService.findByMemberId(member.getId());
//            List<IssuedCoupon> issuedCoupons = member.getIssuedCoupons();
//            model.addAttribute("coupons", issuedCoupons);
//            System.out.println(issuedCoupons.get(0));
//        }

        model.addAttribute("images", images);
        model.addAttribute("product", product);

        return "order_product";
    }

    @PostMapping("/{product_id}")
    public String orderProduct(@PathVariable Long product_id, Model model, HttpSession session,
                               @RequestParam int quantity, @RequestParam Long coupon_id)
    {

        Delivery delivery = new Delivery();
        OrderItem orderItem = new OrderItem();
        Order order = new Order();
        Product product = productService.findById(product_id);
        Optional<Coupon> cp = couponService.findById(coupon_id);

        int price = product.getPrice();
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);

        int totalPrice = price * quantity;

        if(cp.isPresent()) {
            Coupon coupon = cp.get();
            order.setCoupon(coupon);
            if(totalPrice > coupon.getMinOrderAmount()) {

                double discountPer = ((double) coupon.getDiscount() / 100);
                int result = (int)(totalPrice / discountPer);

                if(result > coupon.getMaxOrderAmount()) {
                    result = coupon.getMaxOrderAmount();
                }

                totalPrice -= result;

            }
        }

        order.setTotalAmount(totalPrice);

        String orderNumber = "";
        for(int i = 0; i < 10; i++) {
            orderNumber += Math.round(Math.random() * 10);
        }

        order.setOrderNumber(orderNumber);
        delivery.setTrackingNumber(orderNumber);

        Member member = (Member) session.getAttribute("member");
        if(member != null) {

            order.setMember(member);
            orderService.save(order);
            delivery.setAddress(member.getAddress());
            delivery.setOrder(order);
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItemService.save(orderItem);
            deliveryService.save(delivery);

        }

        return "redirect:/";
    }

}
