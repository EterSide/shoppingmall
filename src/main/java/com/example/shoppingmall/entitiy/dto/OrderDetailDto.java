package com.example.shoppingmall.entitiy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {

    private long member_id;
    private long product_id;
    private int quantity;
    private int price;
    private int discount_price;

}
