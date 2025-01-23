package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Category;
import com.example.shoppingmall.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/register")
    public String register() {
        return "register_category";
    }

    @PostMapping("/register")
    public String registerCategory(Category category) {

        categoryService.save(category);

        return "redirect:/";
    }

}
