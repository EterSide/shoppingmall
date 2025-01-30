package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.*;
import com.example.shoppingmall.entitiy.dto.OrderHistoryDto;
import com.example.shoppingmall.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

            List<IssuedCoupon> issuedCoupons = issuedCouponService.findByMemberAndIsUsed(member.getId(), false);

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
    public String orderProduct(@PathVariable Long product_id, HttpSession session,
                               @RequestParam int quantity, @RequestParam(required = false,defaultValue = "0") Long issued_coupon)
    {

        //상품id랑 수량 그리고 쿠폰 id
        //
        System.out.println("--------------------------");
        System.out.println(issued_coupon);
        Delivery delivery = new Delivery();
        OrderItem orderItem = new OrderItem();
        Order order = new Order();

        Product product = productService.findById(product_id);
        Optional<Coupon> cp = couponService.findById(issued_coupon);

        int price = product.getPrice();
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);

        //쿠폰 적용 전
        int totalPrice = price * quantity;

        Member member1 = (Member) session.getAttribute("member");

        //쿠폰 있으면 적용
        if(cp.isPresent()) {
            Coupon coupon = cp.get();
            IssuedCoupon memberAndCoupon = issuedCouponService.findMemberAndCoupon(member1.getId(), issued_coupon);
            order.setCoupon(coupon);
            if(totalPrice >= coupon.getMinOrderAmount()) {

                double discountPer = ((double) coupon.getDiscount() / 100);
                //int result = (int)(totalPrice / discountPer);
                int result = (int)(totalPrice * discountPer); // 할인 금액

                if(result > coupon.getMaxOrderAmount()) {
                    result = coupon.getMaxOrderAmount();
                }

                totalPrice -= result;

            }
            System.out.println("----------------------------적용");
            memberAndCoupon.setUsed(true);
            memberAndCoupon.setUsedDate(LocalDateTime.now());

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
            product.setStock(product.getStock() - quantity);
            product.setSaleCount(product.getSaleCount() + quantity);
            productService.update(product);
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

    @GetMapping("/complete")
    public String orderComplete() {
        return "order_complete";  // order_complete.html 템플릿을 반환
    }

    @GetMapping("/list")
    public String orderList(HttpSession session,Model model) {
        Member member = (Member) session.getAttribute("member");
        List<Order> orderLists = orderService.findByMemberId(member.getId());
        if (orderLists.isEmpty()) {
            // 주문 항목이 없을 경우 처리
            model.addAttribute("errorMessage", "주문 항목이 없습니다.");
            return "error_page"; // 에러 페이지로 리다이렉트
        }

        List<OrderHistoryDto> allOrderHistory = new ArrayList<>();

        for(Order orderList : orderLists){
            List<OrderItem> orderItems = orderItemService.findByOrderId(orderList.getId());
            Delivery delivery = deliveryService.findByOrderId(orderList.getId());
            int itemCnt = orderItems.size();
            String firstItemName = orderItems.get(0).getProduct().getName();
            System.out.println(delivery.getStatus().toString());

            allOrderHistory.add(
                    new OrderHistoryDto(
                            orderList.getId(),
                            firstItemName+" 외 "+ (itemCnt-1) +"건" ,
                            orderList.getOrderNumber(),
                            delivery.getStatus().toString())
            );

        }

        model.addAttribute("orderLists", allOrderHistory);


        return "order_list";
    }

    @GetMapping("/detail/{order_id}")
    public String orderDetail(HttpSession session,Model model, @PathVariable Long order_id) {

        List<OrderItem> orderItems = orderItemService.findByOrderId(order_id);
        Optional<Order> orderId = orderService.findById(order_id);


        if (orderItems.isEmpty()) {
            // 주문 항목이 없을 경우 처리
            model.addAttribute("errorMessage", "주문 항목이 없습니다.");
            return "error_page"; // 에러 페이지로 리다이렉트
        }

        List<Map<String, Object>> items = orderItems.stream().map(orderItem -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", orderItem.getId());
            item.put("quantity", orderItem.getQuantity());

            Map<String, Object> product = new HashMap<>();
            product.put("id", orderItem.getProduct().getId());
            product.put("name", orderItem.getProduct().getName());
            product.put("price", orderItem.getProduct().getPrice());

            Map<String, Object> order = new HashMap<>();
            order.put("id", orderItem.getId());
            order.put("total_amount", orderItem.getOrder().getTotalAmount());
            order.put("coupon_id", orderItem.getOrder().getCoupon());

            // 이미지 처리
            if (orderItem.getProduct().getImages() != null && !orderItem.getProduct().getImages().isEmpty()) {
                product.put("imageUrl", orderItem.getProduct().getImages().get(0).getImageUrl());
            } else {
                product.put("imageUrl", "https://placehold.co/50x50");
            }

            item.put("product", product);
            return item;
        }).collect(Collectors.toList());

        int allPrice = 0;

        Coupon coupon = new Coupon();

        for(OrderItem orderItem : orderItems) {
            coupon = orderItem.getOrder().getCoupon();
            allPrice += orderItem.getPrice() * orderItem.getQuantity();
        }

        model.addAttribute("coupon", coupon);
        model.addAttribute("allPrice", allPrice);
        model.addAttribute("items", items);
        model.addAttribute("count", items.size());

        orderId.ifPresent(order -> model.addAttribute("finalPrice", order.getTotalAmount()));


        return "order_detail";
    }

}