package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Cart;
import com.example.shoppingmall.entitiy.Category;
import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.service.CartService;
import com.example.shoppingmall.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CategoryService categoryService;
    private final CartService cartService;

    public GlobalControllerAdvice(CategoryService categoryService, CartService cartService) {
        this.categoryService = categoryService;
        this.cartService = cartService;
    }

    @ModelAttribute("categories")
    public List<Category> addCategoriesToModel() {
        return categoryService.findAll(); // 모든 페이지에 categories 데이터 추가
    }

    @ModelAttribute("cartItems")
    public List<Cart> addCartItemsToModel(HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member != null) {
            return cartService.getCartItems(member.getId());
        }
        return new ArrayList<>();
    }
}
