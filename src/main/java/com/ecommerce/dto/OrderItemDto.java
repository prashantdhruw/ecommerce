package com.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal priceAtPurchase;
}