package com.example.shoppingmall.entitiy;

import com.example.shoppingmall.entitiy.status.ProductStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT") // DB 컬럼 정보를 직접 준다.
    private String description;

    @Column(nullable = false)
    private int price;

    private int stock;

    private int saleCount; // 상품의 판매량

    private int viewCount; // 조회수

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> images = new ArrayList<>();

}
