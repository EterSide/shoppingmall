package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Order;
import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.entitiy.ProductImage;
import com.example.shoppingmall.service.OrderService;
import com.example.shoppingmall.service.ProductImageService;
import com.example.shoppingmall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;
    private final ProductImageService productImageService;

    @GetMapping("/{product_id}")
    public String orderProduct(@PathVariable Long product_id, Model model, Order order) {

        Product product = productService.findById(product_id);
        List<ProductImage> images = productImageService.findByProductId(product.getId());
        model.addAttribute("images", images);
        model.addAttribute("product", product);

        return "order_product";
    }

    @PostMapping("/{product_id}")
    public String orderProduct(@ModelAttribute Order order, Model model) {
        return "redirect:/";
    }

}
