package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.IssuedCoupon;
import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.service.CartService;
import com.example.shoppingmall.service.IssuedCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.entitiy.Cart;
import com.example.shoppingmall.entitiy.Order;
import com.example.shoppingmall.entitiy.OrderItem;
import com.example.shoppingmall.entitiy.Delivery;
import com.example.shoppingmall.service.OrderService;
import com.example.shoppingmall.service.CouponService;
import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.service.ProductService;
import com.example.shoppingmall.service.OrderItemService;
import com.example.shoppingmall.service.DeliveryService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final IssuedCouponService issuedCouponService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final ProductService productService;
    private final OrderItemService orderItemService;
    private final DeliveryService deliveryService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestParam Long productId, 
                                     @RequestParam int quantity,
                                     HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        
        cartService.addToCart(member.getId(), productId, quantity);
        return ResponseEntity.ok().body("장바구니에 추가되었습니다.");
    }

    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return "redirect:/login";
        }
        
        List<Cart> cartItems = cartService.getCartItems(member.getId());
        int totalPrice = cartService.calculateTotalPrice(cartItems);

        List<IssuedCoupon> issuedCoupons = issuedCouponService.findByMemberAndIsUsed(member.getId(), false);

        model.addAttribute("coupons", issuedCoupons);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cart_list";
    }

    @PostMapping("/update-quantity")
    @ResponseBody
    public ResponseEntity<?> updateQuantity(@RequestParam Long cartItemId,
                                          @RequestParam int quantity,
                                          HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        
        Cart updatedCart = cartService.updateCartItemQuantity(cartItemId, quantity);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "수량이 업데이트되었습니다.");
        response.put("quantity", updatedCart.getQuantity());
        response.put("totalPrice", updatedCart.getQuantity() * updatedCart.getProduct().getPrice());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-selected")
    @ResponseBody
    public ResponseEntity<?> updateSelected(@RequestParam Long cartItemId,
                                          @RequestParam boolean selected,
                                          HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        
        cartService.updateCartItemSelected(cartItemId, selected);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items")
    @ResponseBody
    public ResponseEntity<?> getCartItems(HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        
        List<Cart> cartItems = cartService.getCartItems(member.getId());
        
        // DTO로 변환하여 반환
        List<Map<String, Object>> items = cartItems.stream().map(cart -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", cart.getId());
            item.put("quantity", cart.getQuantity());
            item.put("selected", cart.isSelected());
            
            Map<String, Object> product = new HashMap<>();
            product.put("id", cart.getProduct().getId());
            product.put("name", cart.getProduct().getName());
            product.put("price", cart.getProduct().getPrice());
            
            // 이미지 처리
            if (cart.getProduct().getImages() != null && !cart.getProduct().getImages().isEmpty()) {
                product.put("imageUrl", cart.getProduct().getImages().get(0).getImageUrl());
            } else {
                product.put("imageUrl", "https://placehold.co/50x50");
            }
            
            item.put("product", product);
            return item;
        }).collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("count", items.size());
        
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/order")
//    public String orderCart(HttpSession session, Model model) {
//
//        Member member = (Member) session.getAttribute("member");
//        List<Cart> cartItems = cartService.getCartItems(member.getId());
//        List<Product> products = new ArrayList<>();
//
//        for(Cart cart : cartItems) {
//            products.add(cart.getProduct());
//        }
//
//        model.addAttribute("cartItems", cartItems);
//        model.addAttribute("products", products);
//
//        return "order_cart";
//    }

    //카트에 담긴 상품 주문
    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<?> checkout(@RequestBody Map<String, Long> request, HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Long couponId = request.get("couponId");
        
        try {
            // 선택된 장바구니 아이템만 가져오기
            List<Cart> selectedItems = cartService.getSelectedCartItems(member.getId());
            if (selectedItems.isEmpty()) {
                return ResponseEntity.badRequest().body("선택된 상품이 없습니다.");
            }

            // 주문 생성
            Order order = new Order();
            order.setMember(member);
            order.setOrderNumber(generateOrderNumber());
            
            // 쿠폰 적용
            if (couponId != null && couponId > 0) {
                Optional<Coupon> coupon = couponService.findById(couponId);
                coupon.ifPresent(order::setCoupon);
                IssuedCoupon memberAndCoupon = issuedCouponService.findMemberAndCoupon(member.getId(), couponId);
                memberAndCoupon.setUsed(true);
                memberAndCoupon.setUsedDate(LocalDateTime.now());
            }

            // 총 주문 금액 계산
            int totalAmount = calculateTotalAmount(selectedItems, order.getCoupon());
            order.setTotalAmount(totalAmount);
            
            // 주문 저장
            orderService.save(order);

            // 배송 정보 생성
            Delivery delivery = new Delivery();
            delivery.setOrder(order);
            delivery.setAddress(member.getAddress());
            delivery.setTrackingNumber(order.getOrderNumber());
            deliveryService.save(delivery);

            // 주문 상품 정보 생성
            for (Cart cartItem : selectedItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getProduct().getPrice());
                orderItemService.save(orderItem);

                // 상품 재고 및 판매량 업데이트
                Product product = cartItem.getProduct();
                product.setStock(product.getStock() - cartItem.getQuantity());
                product.setSaleCount(product.getSaleCount() + cartItem.getQuantity());
                productService.update(product);

                // 장바구니에서 제거
                cartService.removeCartItem(cartItem.getId());
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("주문 처리 중 오류가 발생했습니다.");
        }
    }

    private String generateOrderNumber() {
        StringBuilder orderNumber = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            orderNumber.append(Math.round(Math.random() * 10));
        }
        return orderNumber.toString();
    }

    private int calculateTotalAmount(List<Cart> items, Coupon coupon) {
        int totalPrice = items.stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        if (coupon != null && totalPrice >= coupon.getMinOrderAmount()) {
            int discountAmount = (int) (totalPrice * (coupon.getDiscount() / 100.0));
            discountAmount = Math.min(discountAmount, coupon.getMaxOrderAmount());
            return totalPrice - discountAmount;
        }

        return totalPrice;
    }

}
