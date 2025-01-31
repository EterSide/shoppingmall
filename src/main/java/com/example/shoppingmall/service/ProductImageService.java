package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.entitiy.ProductImage;
import com.example.shoppingmall.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;

    public void save(ProductImage productImage) {
        productImageRepository.save(productImage);
    }

    public Optional<ProductImage> findById(long id) {
        return productImageRepository.findById(id);
    }

    public void deleteById(long id) {
        productImageRepository.deleteById(id);
    }

    public List<ProductImage> findByProductId(Long productId) {
        return productImageRepository.findByProductId(productId);
    }

    public int getMaxDisplayOrder(Long productId) {
        // 특정 상품의 이미지들 중 가장 높은 displayOrder를 조회
        // 이미지가 없는 경우 0을 반환
        return productImageRepository.findMaxDisplayOrderByProductId(productId)
                .orElse(0);
    }
}
