package com.ecommerce.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
/**
 * DTO for user signup requests.
 * Includes username, email, password, and user role.
 * The role must be either "CUSTOMER" or "ADMIN".
 */
public class SignupRequest {

    @Schema(description = "Unique username", example = "john_doe")
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Schema(description = "User email address", example = "john@example.com")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "User password", example = "P@ssw0rd123")
    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
    @Schema(description = "User role, must be either 'CUSTOMER' or 'ADMIN'", example = "CUSTOMER")
    @NotBlank(message = "Role is required")
    @jakarta.validation.constraints.Pattern(
        regexp = "^(CUSTOMER|ADMIN)$",
        message = "Role must be either 'CUSTOMER' or 'ADMIN'"
    )
    private String role;
}