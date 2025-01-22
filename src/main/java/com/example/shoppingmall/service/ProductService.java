package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void save(Product product) {

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
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}
