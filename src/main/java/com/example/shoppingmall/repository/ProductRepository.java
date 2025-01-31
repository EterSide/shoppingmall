package com.example.shoppingmall.repository;

import com.example.shoppingmall.entitiy.Category;
import com.example.shoppingmall.entitiy.Product;
import com.example.shoppingmall.entitiy.status.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    public List<Product> findByCategory(Category category);

    public List<Product> findAllByStatus(ProductStatus status);

    @Query(value = "SELECT p FROM Product p ORDER BY p.saleCount DESC LIMIT :limit")
    List<Product> findTopNByOrderBySalesCountDesc(@Param("limit") int limit);
    
    @Query(value = "SELECT p FROM Product p ORDER BY p.createdAt DESC LIMIT :limit")
    List<Product> findTopNByOrderByCreatedAtDesc(@Param("limit") int limit);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    @Query(value = "SELECT p FROM Product p WHERE p.category = :category ORDER BY p.saleCount DESC LIMIT :limit")
    List<Product> findTopProductsByCategoryOrderBySaleCountDesc(@Param("category") Category category, @Param("limit") int limit);
}
