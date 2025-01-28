package com.example.shoppingmall.entitiy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderHistoryDto {

    private long orderId;
    private String orderName; //상품1개 + 외 몇 건
    private String orderNumber;
    private String deliveryStatus;

}
