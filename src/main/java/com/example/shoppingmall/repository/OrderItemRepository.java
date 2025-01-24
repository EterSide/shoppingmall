package com.example.shoppingmall.repository;

import com.example.shoppingmall.entitiy.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
