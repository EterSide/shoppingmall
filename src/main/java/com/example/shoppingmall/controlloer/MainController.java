package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.service.ProductRecommendationListener;
import com.example.shoppingmall.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final ProductService productService;
    private final ProductRecommendationListener recommendationListener;



    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        // 기본 상품 목록 조회
        List<Product> popularProducts = productService.getPopularProducts(8);
        List<Product> newProducts = productService.getNewProducts(8);
        
        // 추천 상품 목록 설정
        List<Product> recommendedProducts = getRecommendedProducts(session, popularProducts);
        
        // 모델에 데이터 추가
        addProductsToModel(model, popularProducts, newProducts, recommendedProducts);
        
        return "index";
    }

    private List<Product> getRecommendedProducts(HttpSession session, List<Product> fallbackProducts) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return fallbackProducts;
        }

        List<Long> recommendedProductIds = recommendationListener.getRecommendationsForUser(
            member.getId().toString()
        );

        return recommendedProductIds.isEmpty()
            ? fallbackProducts
            : recommendedProductIds.stream()
                .map(productService::findById)
                .toList();
    }

    private void addProductsToModel(
        Model model, 
        List<Product> popularProducts, 
        List<Product> newProducts, 
        List<Product> recommendedProducts
    ) {
        model.addAttribute("popularProducts", popularProducts);
        model.addAttribute("newProducts", newProducts);
        model.addAttribute("recommendedProducts", recommendedProducts);
    }

}
