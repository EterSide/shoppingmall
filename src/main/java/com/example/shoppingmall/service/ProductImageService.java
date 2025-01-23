package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.entitiy.ProductImage;
import com.example.shoppingmall.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;

    public void save(ProductImage productImage) {
        productImageRepository.save(productImage);

    }

    public List<ProductImage> findByProductId(Long productId) {
        return productImageRepository.findByProductId(productId);
    }
}
