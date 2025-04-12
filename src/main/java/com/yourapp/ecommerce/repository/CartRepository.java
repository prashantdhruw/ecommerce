package com.yourapp.ecommerce.repository;

import com.yourapp.ecommerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    Optional<Cart> findByUser(com.yourapp.ecommerce.entity.User user);
}