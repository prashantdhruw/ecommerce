package com.yourapp.ecommerce.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class LoginRequest {

    @NotBlank
    @jakarta.validation.constraints.Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
}