package com.example.shoppingmall.repository;

import com.example.shoppingmall.entitiy.Category;
import com.example.shoppingmall.entitiy.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    public List<Product> findByCategory(Category category);

}
