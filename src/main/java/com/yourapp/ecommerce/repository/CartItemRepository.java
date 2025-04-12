package com.yourapp.ecommerce.repository;

import com.yourapp.ecommerce.entity.Cart;
import com.yourapp.ecommerce.entity.CartItem;
import com.yourapp.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}