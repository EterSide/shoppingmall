package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Delivery;
import com.example.shoppingmall.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public Delivery save(Delivery delivery) {
        return deliveryRepository.save(delivery);
    }

}
