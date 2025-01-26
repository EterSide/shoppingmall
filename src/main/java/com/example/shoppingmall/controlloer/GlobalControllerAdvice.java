package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Category;
import com.example.shoppingmall.service.CategoryService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CategoryService categoryService;

    public GlobalControllerAdvice(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ModelAttribute("categories")
    public List<Category> addCategoriesToModel() {
        return categoryService.findAll(); // 모든 페이지에 categories 데이터 추가
    }
}
