package com.ecommerce.controller;

import com.ecommerce.dto.AddItemRequest;
import com.ecommerce.dto.CartDto;
import com.ecommerce.dto.UpdateItemRequest;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<CartDto> getCart(Authentication authentication) {
        User user = getCurrentUser(authentication);
        CartDto cartDto = cartService.getCart(user);
        return ResponseEntity.ok(cartDto);
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItem(
            Authentication authentication,
            @Valid @RequestBody AddItemRequest request) {
        User user = getCurrentUser(authentication);
        cartService.addProductToCart(user, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<Void> updateItem(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateItemRequest request) {
        User user = getCurrentUser(authentication);
        cartService.updateCartItemQuantity(user, productId, request.getQuantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(
            Authentication authentication,
            @PathVariable Long productId) {
        User user = getCurrentUser(authentication);
        cartService.removeProductFromCart(user, productId);
        return ResponseEntity.ok().build();
    }
}