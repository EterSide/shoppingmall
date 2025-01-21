package com.example.shoppingmall.entitiy;

import com.example.shoppingmall.entitiy.status.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address; // 주소

    private String trackingNumber; // 송장번호

    private LocalDateTime completeAt;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status = DeliveryStatus.PREPARING;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
