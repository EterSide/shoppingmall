package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Category;
import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product save(Product product) {

        Product pro = new Product();
        pro.setName(product.getName());
        pro.setPrice(product.getPrice());
        pro.setCategory(product.getCategory());
        pro.setDescription(product.getDescription());
        pro.setImages(product.getImages());
        pro.setSaleCount(product.getSaleCount());
        pro.setStock(product.getStock());
        pro.setViewCount(product.getViewCount());

         productRepository.save(pro);
         return pro;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void update(Product product) {
        productRepository.save(product);
    }

    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getPopularProducts(int limit) {
        // 판매량이 많은 순으로 상품 조회
        return productRepository.findTopNByOrderBySalesCountDesc(limit);
    }

    public List<Product> getNewProducts(int limit) {
        // 최신 등록순으로 상품 조회
        return productRepository.findTopNByOrderByCreatedAtDesc(limit);
    }

    public List<Product> searchByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Product> findTopProductsByCategory(Category category, int limit) {
        return productRepository.findTopProductsByCategoryOrderBySaleCountDesc(category, limit);
    }
}
