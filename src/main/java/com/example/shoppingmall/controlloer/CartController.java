package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.IssuedCoupon;
import com.example.shoppingmall.service.CartService;
import com.example.shoppingmall.service.IssuedCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.entitiy.Cart;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final IssuedCouponService issuedCouponService;

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

        List<IssuedCoupon> issuedCoupons = issuedCouponService.findByMemberId(member.getId());

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
            
            Map<String, Object> product = new HashMap<>();
            product.put("id", cart.getProduct().getId());
            product.put("name", cart.getProduct().getName());
            product.put("price", cart.getProduct().getPrice());
            
            // 이미지 처리
            if (!cart.getProduct().getImages().isEmpty()) {
                product.put("imageUrl", cart.getProduct().getImages().get(0).getImageUrl());
            }
            
            item.put("product", product);
            return item;
        }).collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("count", items.size());
        
        return ResponseEntity.ok(response);
    }


}
