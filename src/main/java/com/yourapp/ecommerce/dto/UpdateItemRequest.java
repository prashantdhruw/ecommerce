package com.yourapp.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateItemRequest {

    @NotNull
    @Min(1)
    private Integer quantity;

    // Getters and setters

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}