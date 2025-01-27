package com.example.shoppingmall.entitiy;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Getter
@Setter  // @Data 제거
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl; // 이미지 주소

    private int displayOrder; // 이미지 순서

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
}
