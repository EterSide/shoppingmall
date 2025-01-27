package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    ProductService productService;

    @GetMapping("/")
    public String index(Model model) {


        List<Product> popularProducts = productService.getPopularProducts(8);
        // 신상품 8개 조회
        List<Product> newProducts = productService.getNewProducts(8);

        model.addAttribute("popularProducts", popularProducts);
        model.addAttribute("newProducts", newProducts);

        return "index";
    }

}
