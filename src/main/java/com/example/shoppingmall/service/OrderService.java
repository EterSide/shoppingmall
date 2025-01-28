package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Order;
import com.example.shoppingmall.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> findByMemberId(Long memberId) {
        return orderRepository.findByMemberId(memberId);

    }

    public Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }


}
