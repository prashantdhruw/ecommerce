package com.yourapp.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShippingDetailsDto {

    @NotBlank
    private String address;
}
