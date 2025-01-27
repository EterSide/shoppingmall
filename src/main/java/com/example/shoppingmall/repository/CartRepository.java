package com.example.shoppingmall.repository;

import com.example.shoppingmall.entitiy.Cart;
import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.entitiy.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByMemberId(Long memberId);
    Optional<Cart> findByMemberAndProduct(Member member, Product product);
    List<Cart> findByMemberIdAndSelectedTrue(Long memberId);
    void deleteById(Long id);
}
