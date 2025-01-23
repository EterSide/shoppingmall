package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Category;
import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category save(Category category) {

        List<Category> categories = categoryRepository.findAll();

        for(Category cate : categories) {
            if(cate.getName().equals(category.getName())) {
                return null;
            }
        }
        return categoryRepository.save(category);
    }

}
