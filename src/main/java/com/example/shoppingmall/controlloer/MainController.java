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


//    @GetMapping("/")
//    public String index(Model model, HttpSession session) {
//// 기존 인기상품, 신상품 조회
//        List<Product> popularProducts = productService.getPopularProducts(8);
//        List<Product> newProducts = productService.getNewProducts(8);
//        List<Product> recommendedProducts = new ArrayList<>();
//        // 사용자별 추천상품 조회
//        String userId = session.getId();
//        List<Long> recommendedProducts_id = recommendationListener.getRecommendationsForUser(userId);
//        if(recommendedProducts_id != null) {
//            recommendedProducts_id.forEach(product -> {
//                recommendedProducts.add(productService.findById(product));
//            });
//            model.addAttribute("recommendedProducts", recommendedProducts);
//        }
//
//
//
//        model.addAttribute("popularProducts", popularProducts);
//        model.addAttribute("newProducts", newProducts);
//
//        model.addAttribute("recommendedProducts", popularProducts);
//
//
//
//        return "index";
//    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        // 기존 인기상품, 신상품 조회
        List<Product> popularProducts = productService.getPopularProducts(8);
        List<Product> newProducts = productService.getNewProducts(8);

        // 사용자별 추천상품 조회
        Member member = (Member) session.getAttribute("member");
        if (member != null) {
            Long userId = member.getId();

            List<Long> recommendedProductIds = recommendationListener.getRecommendationsForUser(userId.toString());
            System.out.println(recommendedProductIds);
            // 추천 상품이 있는 경우에만 처리
            if (!recommendedProductIds.isEmpty()) {
                List<Product> recommendedProducts = recommendedProductIds.stream()
                    .map(productService::findById)  // 명시적으로 Long으로 변환
                    .toList();
            model.addAttribute("recommendedProducts", recommendedProducts);
            } else {
                // 추천 상품이 없으면 인기상품으로 대체
                model.addAttribute("recommendedProducts", popularProducts);
            }

            model.addAttribute("popularProducts", popularProducts);
            model.addAttribute("newProducts", newProducts);
        }else{
            model.addAttribute("popularProducts", popularProducts);
            model.addAttribute("newProducts", newProducts);
            model.addAttribute("recommendedProducts", popularProducts);
        }


        return "index";
    }

}
