package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Cart;
import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final MemberService memberService;
    private final ProductService productService;

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart addToCart(Long memberId, Long productId, int quantity) {
        Member member = memberService.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        Product product = productService.findById(productId);
        
        // 이미 장바구니에 있는지 확인
        Optional<Cart> existingCart = cartRepository.findByMemberAndProduct(member, product);
        
        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            return cartRepository.save(cart);
        }

        Cart cart = new Cart();
        cart.setMember(member);
        cart.setProduct(product);
        cart.setQuantity(quantity);
        cart.setSelected(true);
        
        return cartRepository.save(cart);
    }

    public List<Cart> getCartItems(Long memberId) {
        return cartRepository.findByMemberId(memberId);
    }

    public int calculateTotalPrice(List<Cart> cartItems) {
        return cartItems.stream()
                .filter(Cart::isSelected)
                .mapToInt(cart -> cart.getProduct().getPrice() * cart.getQuantity())
                .sum();
    }

    public Cart updateCartItemQuantity(Long cartItemId, int quantity) {
        Cart cartItem = cartRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("장바구니 아이템을 찾을 수 없습니다."));
        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    public void updateCartItemSelected(Long cartItemId, boolean selected) {
        Cart cartItem = cartRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("장바구니 아이템을 찾을 수 없습니다."));
        cartItem.setSelected(selected);
        cartRepository.save(cartItem);
    }
}
